package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffAnnualLeaveHistory;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchPersonCertificateDto;
import com.globits.hr.dto.search.SearchStaffAnnualLeaveHistoryDto;
import com.globits.hr.dto.staff.StaffInsuranceHistoryDto;
import com.globits.hr.repository.StaffAnnualLeaveHistoryRepository;
import com.globits.hr.repository.StaffMonthlyLeaveHistoryRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffAnnualLeaveHistoryService;
import com.globits.hr.service.StaffMonthlyLeaveHistoryService;
import com.globits.hr.service.UserExtService;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class StaffAnnualLeaveHistoryServiceImpl extends GenericServiceImpl<StaffAnnualLeaveHistory, UUID> implements StaffAnnualLeaveHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(StaffAnnualLeaveHistoryServiceImpl.class);

    @Autowired
    private StaffAnnualLeaveHistoryRepository staffAnnualLeaveHistoryRepository;

    @Autowired
    private StaffMonthlyLeaveHistoryRepository staffMonthlyLeaveHistoryRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffMonthlyLeaveHistoryService staffMonthlyLeaveHistoryService;

    @Autowired
    private UserExtService userExtService;

    @Override
    public SearchStaffAnnualLeaveHistoryDto getInitialFilter() {
        SearchStaffAnnualLeaveHistoryDto response = new SearchStaffAnnualLeaveHistoryDto();

        response.setPageIndex(1);
        response.setPageSize(10);
        int currentYear = LocalDate.now().getYear();
//        response.setYearReport(currentYear);


        Staff staff = userExtService.getCurrentStaffEntity();
        if (staff == null) {
            return response;
        }

        response.setStaff(new StaffDto());
        response.getStaff().setId(staff.getId());
        response.getStaff().setStaffCode(staff.getStaffCode());
        response.getStaff().setDisplayName(staff.getDisplayName());
        response.setStaffId(response.getStaff().getId());

        if (staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
            Position mainPosition = null;

            for (Position position : staff.getCurrentPositions()) {
                if (position != null && position.getIsMain() != null && position.getIsMain().equals(true)) {
                    mainPosition = position;
                    break;
                }
            }

            if (mainPosition != null) {

                if (mainPosition.getTitle() != null) {
                    PositionTitleDto positionTitle = new PositionTitleDto();

                    positionTitle.setId(mainPosition.getTitle().getId());
                    positionTitle.setCode(mainPosition.getTitle().getCode());
                    positionTitle.setName(mainPosition.getTitle().getName());

                    response.setPositionTitle(positionTitle);
                    response.setPositionTitleId(positionTitle.getId());
                }

                if (mainPosition.getDepartment() != null) {
                    HRDepartmentDto department = new HRDepartmentDto();

                    department.setId(mainPosition.getDepartment().getId());
                    department.setCode(mainPosition.getDepartment().getCode());
                    department.setName(mainPosition.getDepartment().getName());

                    response.setDepartment(department);
                    response.setDepartmentId(department.getId());
                }

                if (mainPosition.getDepartment() != null && mainPosition.getDepartment().getOrganization() != null) {
                    HrOrganizationDto organization = new HrOrganizationDto();

                    organization.setId(mainPosition.getDepartment().getOrganization().getId());
                    organization.setCode(mainPosition.getDepartment().getOrganization().getCode());
                    organization.setName(mainPosition.getDepartment().getOrganization().getName());

                    response.setOrganization(organization);
                    response.setOrganizationId(organization.getId());
                }
            }
        }

        return response;
    }

    @Override
    public Page<StaffAnnualLeaveHistoryDto> searchByPage(SearchStaffAnnualLeaveHistoryDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        String whereClause = " WHERE (1=1) ";
        String orderBy = " ORDER BY entity.year DESC, entity.modifyDate desc ";

        String sqlCount = "SELECT COUNT(entity.id) FROM StaffAnnualLeaveHistory entity ";
        String sql = "SELECT new com.globits.hr.dto.StaffAnnualLeaveHistoryDto(entity) FROM StaffAnnualLeaveHistory entity ";

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;

        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
            hasJoinMainPosition = true;
        }

        if (StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.staff.displayName LIKE :text " +
                    " OR entity.staff.displayName LIKE :text " +
                    " OR entity.staff.staffCode LIKE :text) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " AND entity.staff.id = :staffId ";
        }

        if (dto.getYearReport() != null) {
            whereClause += " AND entity.year = :yearReport ";
        }

        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                whereClause += " AND (pos.department.organization.id = :organizationId) ";
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                whereClause += " AND (pos.department.id = :departmentId) ";
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                whereClause += " AND (pos.title.id = :positionTitleId) ";
            }
        }

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;

        Query query = manager.createQuery(sql, StaffInsuranceHistoryDto.class);
        Query countQuery = manager.createQuery(sqlCount);

        if (StringUtils.hasText(dto.getKeyword())) {
            String keyword = "%" + dto.getKeyword().trim() + "%";
            query.setParameter("text", keyword);
            countQuery.setParameter("text", keyword);
        }

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            countQuery.setParameter("staffId", dto.getStaffId());
        }

        if (dto.getYearReport() != null) {
            query.setParameter("yearReport", dto.getYearReport());
            countQuery.setParameter("yearReport", dto.getYearReport());
        }

        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                query.setParameter("organizationId", dto.getOrganizationId());
                countQuery.setParameter("organizationId", dto.getOrganizationId());
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                query.setParameter("departmentId", dto.getDepartmentId());
                countQuery.setParameter("departmentId", dto.getDepartmentId());
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", dto.getPositionTitleId());
                countQuery.setParameter("positionTitleId", dto.getPositionTitleId());
            }
        }


        long total = (long) countQuery.getSingleResult();

        query.setFirstResult(pageIndex * pageSize);
        query.setMaxResults(pageSize);

        List<StaffAnnualLeaveHistoryDto> content = query.getResultList();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public StaffAnnualLeaveHistoryDto getById(UUID id) {
        if (id == null)
            return null;
        StaffAnnualLeaveHistory entity = staffAnnualLeaveHistoryRepository.findById(id).orElse(null);

        if (entity == null)
            return null;
        StaffAnnualLeaveHistoryDto response = new StaffAnnualLeaveHistoryDto(entity, true);

        return response;
    }

    @Override
    public StaffAnnualLeaveHistoryDto saveOrUpdate(StaffAnnualLeaveHistoryDto dto) {
        if (dto == null || dto.getYear() == null) {
            return null;
        }


        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        } else if (dto.getStaffId() != null) {
            staff = staffRepository.findById(dto.getStaffId()).orElse(null);
        }

        if (staff == null) {
            return null; // hoặc throw exception nếu cần thông báo lỗi rõ hơn
        }

        StaffAnnualLeaveHistory entity = null;

        if (dto.getId() != null) {
            entity = staffAnnualLeaveHistoryRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null && dto.getYear() != null) {
            List<StaffAnnualLeaveHistory> availableResults = staffAnnualLeaveHistoryRepository.findByStaffIdAndYear(staff.getId(), dto.getYear());
            if (availableResults != null && !availableResults.isEmpty()) {
                entity = availableResults.get(0);
            }
        }
        if (entity == null) {
            entity = new StaffAnnualLeaveHistory();
        }

        entity.setStaff(staff);
        entity.setYear(dto.getYear());
        entity.setGrantedLeaveDays(dto.getGrantedLeaveDays());
        entity.setGrantedLeaveDaysNote(dto.getGrantedLeaveDaysNote());
        entity.setCarriedOverLeaveDays(dto.getCarriedOverLeaveDays());
        entity.setCarriedOverLeaveDaysNote(dto.getCarriedOverLeaveDaysNote());
        entity.setSeniorityLeaveDays(dto.getSeniorityLeaveDays());
        entity.setSeniorityLeaveDaysNote(dto.getSeniorityLeaveDaysNote());
        entity.setBonusLeaveDays(dto.getBonusLeaveDays());
        entity.setBonusLeaveDaysNote(dto.getBonusLeaveDaysNote());
        entity.setCancelledLeaveDays(dto.getCancelledLeaveDays());
        entity.setCancelledLeaveDaysNote(dto.getCancelledLeaveDaysNote());

        staffMonthlyLeaveHistoryService.handleSetMonthlyLeaveHistoryForAnnualLeave(entity, dto);

        entity = staffAnnualLeaveHistoryRepository.save(entity);

        return new StaffAnnualLeaveHistoryDto(entity, true);
    }


    @Override
    public Boolean deleteById(UUID id) {
        if (id == null)
            return false;

        StaffAnnualLeaveHistory entity = staffAnnualLeaveHistoryRepository.findById(id).orElse(null);
        if (entity == null)
            return false;

        staffAnnualLeaveHistoryRepository.delete(entity);
        return true;
    }

    @Override
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteById(itemId);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }
}
