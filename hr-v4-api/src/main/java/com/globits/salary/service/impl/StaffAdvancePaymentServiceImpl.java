package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.StaffAdvancePayment;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.StaffAdvancePaymentDto;
import com.globits.salary.dto.search.SearchStaffAdvancePaymentDto;
import com.globits.salary.repository.SalaryPeriodRepository;
import com.globits.salary.repository.StaffAdvancePaymentRepository;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.salary.service.StaffAdvancePaymentService;
import com.globits.timesheet.dto.search.SearchWorkScheduleCalendarDto;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffAdvancePaymentServiceImpl extends GenericServiceImpl<StaffAdvancePayment, UUID>
        implements StaffAdvancePaymentService {

    @Autowired
    private StaffAdvancePaymentRepository staffAdvancePaymentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Autowired
    private UserExtService userExtService;

    @Override
    public StaffAdvancePaymentDto saveStaffAdvancePayment(StaffAdvancePaymentDto dto) {
        if (dto == null) {
            return null;
        }
        StaffAdvancePayment entity = null;
        if (dto.getId() != null) {
            entity = staffAdvancePaymentRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new StaffAdvancePayment();
        }
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            Staff staff = staffRepository.findOneById(dto.getStaff().getId());
            if (staff == null)
                return null;
            entity.setStaff(staff);
        } else {
            entity.setStaff(null);
        }
        if (dto.getSalaryPeriod() != null && dto.getSalaryPeriod().getId() != null) {
            SalaryPeriod salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
            if (salaryPeriod == null)
                return null;
            entity.setSalaryPeriod(salaryPeriod);
        } else {
            entity.setStaff(null);
        }
        entity.setRequestDate(dto.getRequestDate());
        entity.setRequestReason(dto.getRequestReason());
        entity.setAdvancedAmount(dto.getAdvancedAmount());
        if (dto.getApprovalStatus() != null) {
            entity.setApprovalStatus(dto.getApprovalStatus());
        } else {
            entity.setApprovalStatus(HrConstants.StaffAdvancePaymentApprovalStatus.NOT_APPROVED_YET.getValue());
        }


        StaffAdvancePayment response = staffAdvancePaymentRepository.save(entity);
        this.processAdvanceSalaryForPayroll(response);
        return new StaffAdvancePaymentDto(response);
    }

    /*
     * save StaffAdvancePayment
     * => tính SUM(advancedAmount) theo SalaryPeriod
     * => tạo và cập nhật thành phần tạm ứng trong phiếu lương theo kì lương
     */
    private void processAdvanceSalaryForPayroll(StaffAdvancePayment entity) {
        Staff staff = entity.getStaff();
        SalaryPeriod salaryPeriod = entity.getSalaryPeriod();
        Double totalAdvancedAmount = 0.0;
        totalAdvancedAmount += staffAdvancePaymentRepository
                .getTotalAdvancedAmountByStaffAndSalaryPeriod(staff.getId(), salaryPeriod.getId(), HrConstants.StaffAdvancePaymentApprovalStatus.APPROVED.getValue());

        // số giờ làm việc
        if (staff != null && salaryPeriod != null) {
            salaryResultStaffItemService.createAndImportSalaryResultItem(staff, salaryPeriod, totalAdvancedAmount,
                    HrConstants.SalaryItemAutoConnectCode.DA_TAM_UNG.getValue()
//    				HrConstants.SalaryAutoMapField.TAM_UNG
            );
        }
    }

    @Override
    public Boolean deleteStaffAdvancePayment(UUID id) {
        if (id == null) {
            return false;
        }
        Optional<StaffAdvancePayment> optionalEntity = staffAdvancePaymentRepository.findById(id);
        if (!optionalEntity.isPresent()) {
            return false;
        }
        StaffAdvancePayment entity = optionalEntity.get();
        staffAdvancePaymentRepository.delete(entity);
        this.processAdvanceSalaryForPayroll(entity);
        return true;
    }

    @Override
    public StaffAdvancePaymentDto getStaffAdvancePayment(UUID id) {
        if (id == null) {
            return null;
        }
        StaffAdvancePayment entity = staffAdvancePaymentRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new StaffAdvancePaymentDto(entity);
    }

    @Override
    public Boolean updateStaffAdvancePaymentApprovalStatus(SearchStaffAdvancePaymentDto dto) throws Exception {
        if (dto == null) {
            return false;
        }
        if (dto.getChosenRecordIds() != null && dto.getChosenRecordIds().isEmpty()) {
            return true;
        }
        for (UUID recordId : dto.getChosenRecordIds()) {
            StaffAdvancePayment entity = staffAdvancePaymentRepository.findById(recordId).orElse(null);
            if (entity == null) {
                throw new Exception("Record is not existed!");
            }
            entity.setApprovalStatus(dto.getApprovalStatus());
            entity = staffAdvancePaymentRepository.save(entity);
            this.processAdvanceSalaryForPayroll(entity);
        }
        return true;
    }

    @Override
    public Page<StaffAdvancePaymentDto> searchByPage(SearchStaffAdvancePaymentDto dto) {
        if (dto == null) {
            return null;
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate desc ";
        String sqlCount = "select count(distinct entity.id) from StaffAdvancePayment as entity ";
        String sql = "select distinct new com.globits.salary.dto.StaffAdvancePaymentDto(entity) from StaffAdvancePayment as entity ";

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
            hasJoinMainPosition = true;
        }

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.staff.staffCode LIKE :text OR entity.staff.firstName LIKE :text  OR entity.staff.lastName LIKE :text OR entity.salaryPeriod.name LIKE :text) ";
        }
        if (dto.getSalaryPeriodId() != null) {
            whereClause += " and (entity.salaryPeriod.id = :salaryPeriodId) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }
        if (dto.getApprovalStatus() != null) {
            whereClause += " and (entity.approvalStatus = :approvalStatus) ";
        }

        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id  =: organizationId ) ";
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id  =: departmentId ) ";
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id  =: positionTitleId ) ";
            }
        }

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;

        Query query = manager.createQuery(sql, StaffAdvancePaymentDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getSalaryPeriodId() != null) {
            query.setParameter("salaryPeriodId", dto.getSalaryPeriodId());
            qCount.setParameter("salaryPeriodId", dto.getSalaryPeriodId());
        }
        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", dto.getApprovalStatus());
            qCount.setParameter("approvalStatus", dto.getApprovalStatus());
        }

        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                query.setParameter("organizationId", dto.getOrganizationId());
                qCount.setParameter("organizationId", dto.getOrganizationId());
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                query.setParameter("departmentId", dto.getDepartmentId());
                qCount.setParameter("departmentId", dto.getDepartmentId());
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", dto.getPositionTitleId());
                qCount.setParameter("positionTitleId", dto.getPositionTitleId());
            }
        }

        List<StaffAdvancePaymentDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<StaffAdvancePaymentDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteStaffAdvancePayment(itemId);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }


    @Override
    public SearchStaffAdvancePaymentDto getInitialFilter() {
        SearchStaffAdvancePaymentDto response = new SearchStaffAdvancePaymentDto();

        response.setApprovalStatus(0);
        response.setPageIndex(1);
        response.setPageSize(10);

        List<SalaryPeriodDto> activePeriods = salaryPeriodService.getActivePeriodsByDate(new Date());
        if (activePeriods != null && !activePeriods.isEmpty()) {
            SalaryPeriodDto period = activePeriods.get(0);
            response.setSalaryPeriod(period);
            response.setSalaryPeriodId(period.getId());
            response.setFromDate(period.getFromDate());
            response.setToDate(period.getToDate());
        }

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
}
