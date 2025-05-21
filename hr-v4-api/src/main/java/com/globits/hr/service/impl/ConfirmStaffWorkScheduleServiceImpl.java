package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.RoleUtils;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.dto.search.SearchOvertimeRequestDto;
import com.globits.timesheet.repository.LeaveTypeRepository;
import com.globits.timesheet.repository.OvertimeRequestRepository;
import com.globits.timesheet.repository.TimeSheetDetailRepository;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ConfirmStaffWorkScheduleServiceImpl extends GenericServiceImpl<StaffWorkSchedule, UUID> implements ConfirmStaffWorkScheduleService {
    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private StaffHierarchyService staffHierarchyService;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private TimeSheetDetailRepository timeSheetDetailRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private OvertimeRequestRepository overtimeRequestRepository;

    @Autowired
    private AbsenceRequestRepository absenceRequestRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private HRDepartmentRepository hRDepartmentRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Override
    @Transactional
    @Modifying
    public List<UUID> updateApprovalStatus(AbsenceRequestSearchDto dto) {
        if (dto == null || dto.getApprovalStatus() == null || dto.getChosenIds() == null || dto.getChosenIds().isEmpty()) {
            return null;
        }

        List<StaffWorkSchedule> onSaveRequests = new ArrayList<>();
        for (UUID requestId : dto.getChosenIds()) {
            if (requestId == null) return null;

            StaffWorkSchedule entity = staffWorkScheduleRepository.findById(requestId).orElse(null);
            if (entity == null) return null;

            entity.setApprovalStatus(dto.getApprovalStatus());
            onSaveRequests.add(entity);
        }

        if (onSaveRequests.isEmpty()) {
            return null;
        }

        List<StaffWorkSchedule> savedRequests = staffWorkScheduleRepository.saveAllAndFlush(onSaveRequests);

        List<UUID> updatedRequestIds = new ArrayList<>();
        for (StaffWorkSchedule request : savedRequests) {
            updatedRequestIds.add(request.getId());
        }

        return updatedRequestIds;
    }


    public Page<StaffWorkScheduleDto> pagingConfirmStaffWorkSchedule(SearchOvertimeRequestDto dto) {
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

        UserDto userDto = userExtService.getCurrentUser();
        Staff staff = userExtService.getCurrentStaffEntity();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
        boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(userDto, staff);
        boolean isStaffView = RoleUtils.hasRoleHrStaffView(userDto);
        if (!(isAdmin || isManager || isAssignment || isShiftAssignment) && staff != null && dto.getStaffId() == null) {
            dto.setStaffId(staff.getId());
        }
        if (!(isAdmin || isManager || isStaffView)) {
            if (isAssignment && isShiftAssignment && staff != null) {
                List<UUID> managedStaff = staffHierarchyService.getAllManagedStaff(staff.getId(), List.of(staff.getId()));
                dto.setStaffIdList(managedStaff);
            } else {
                if (staff == null) return null;
                dto.setStaffId(staff.getId());
            }
        }

        String orderBy = " ";
        if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy()))
            orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC ";
        else {
            orderBy = " order by entity.workingDate desc, entity.modifyDate desc ";
        }
        String sqlCount = "select count(entity.id) from StaffWorkSchedule as entity ";
        String sql = "select new com.globits.hr.dto.StaffWorkScheduleDto(entity) from StaffWorkSchedule as entity  ";
        String whereClause = "where (1=1) ";

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
            hasJoinMainPosition = true;
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.staff.staffCode like :keyword or entity.staff.firstName like :keyword or entity.staff.lastName like :keyword or entity.staff.displayName like :keyword) ";
        }
        if (dto.getStaffId() != null) {
            whereClause += " AND (entity.staff.id =:staffId) ";
        }

        if (isAssignment && isShiftAssignment) {
            if (dto.getStaffIdList() != null && !dto.getStaffIdList().isEmpty()) {
                whereClause += " AND ( entity.staff.id IN (:staffIdList) ) ";
            }
        }
        if (dto.getFromDate() != null && dto.getToDate() != null) {
            whereClause += " AND date(entity.workingDate) >= date(:fromDate) AND date(entity.workingDate) <= date(:toDate)";
        } else if (dto.getFromDate() != null) {
            whereClause += " AND date(entity.workingDate) >= date(:fromDate)";
        } else if (dto.getToDate() != null) {
            whereClause += " AND date(entity.workingDate) <= date(:toDate)";
        }
        whereClause += " AND (entity.needManagerApproval = true) ";
        Boolean voided = dto.getVoided();
        if (voided == null) {
            whereClause += " AND (entity.voided is null or entity.voided=false)";
        } else if (!voided) {
            whereClause += " AND (entity.voided=false)";
        } else if (voided) {
            whereClause += " AND (entity.voided=true)";
        }
        if (dto.getApprovalStatus() != null) {
            whereClause += " AND ( entity.approvalStatus =: approvalStatus) ";
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

        Query q = manager.createQuery(sql, StaffWorkScheduleDto.class);
        Query qCount = manager.createQuery(sqlCount);
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("keyword", "%" + dto.getKeyword() + "%");
            qCount.setParameter("keyword", "%" + dto.getKeyword() + "%");
        }
        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (isAssignment && isShiftAssignment) {
            if (dto.getStaffIdList() != null && !dto.getStaffIdList().isEmpty()) {
                q.setParameter("staffIdList", dto.getStaffIdList());
                qCount.setParameter("staffIdList", dto.getStaffIdList());
            }
        }
        if (dto.getFromDate() != null) {
            q.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            q.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }
        if (dto.getApprovalStatus() != null) {
            q.setParameter("approvalStatus", dto.getApprovalStatus());
            qCount.setParameter("approvalStatus", dto.getApprovalStatus());
        }

        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                q.setParameter("organizationId", dto.getOrganizationId());
                qCount.setParameter("organizationId", dto.getOrganizationId());
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                q.setParameter("departmentId", dto.getDepartmentId());
                qCount.setParameter("departmentId", dto.getDepartmentId());
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                q.setParameter("positionTitleId", dto.getPositionTitleId());
                qCount.setParameter("positionTitleId", dto.getPositionTitleId());
            }
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<StaffWorkScheduleDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public SearchOvertimeRequestDto getInitialFilter() {
        SearchOvertimeRequestDto response = new SearchOvertimeRequestDto();

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
