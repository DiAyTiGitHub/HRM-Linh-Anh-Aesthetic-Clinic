package com.globits.timesheet.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.*;
import com.globits.hr.repository.ShiftWorkRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.hr.service.StaffWorkScheduleService;
import com.globits.hr.service.UserExtService;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.domain.ShiftChangeRequest;
import com.globits.timesheet.dto.ShiftChangeRequestDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.dto.search.ShiftChangeRequestSearchDto;
import com.globits.timesheet.repository.ShiftChangeRequestRepository;
import com.globits.timesheet.service.ShiftChangeRequestService;
import jakarta.persistence.Column;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftChangeRequestServiceImpl extends GenericServiceImpl<ShiftChangeRequest, UUID>
        implements ShiftChangeRequestService {

    @Autowired
    private ShiftChangeRequestRepository repository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Override
    public ShiftChangeRequestDto getById(UUID id) {
        if (id != null) {
            ShiftChangeRequest entity = repository.findById(id).orElse(null);
            if (entity != null) {
                return new ShiftChangeRequestDto(entity);
            }
        }
        return null;
    }

    @Override
    public ShiftChangeRequest getEntityById(UUID id) {
        if (id != null) {
            return repository.findById(id).orElse(null);
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) return false;
        repository.deleteById(id);
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

    @Override
    public ShiftChangeRequestSearchDto getInitialFilter() {
        ShiftChangeRequestSearchDto response = new ShiftChangeRequestSearchDto();

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

    @Override
    public int markDelete(UUID id) {
        Query q = manager.createQuery("update ShiftChangeRequest s set s.voided=true where s.id=:id");
        q.setParameter("id", id);
        return q.executeUpdate();
    }

//	public int markDeleteList(List<UUID> ids) {
//	    Query q = manager.createQuery("update ShiftChangeRequest s set s.voided = true where s.id in :ids");
//	    q.setParameter("ids", ids);
//	    return q.executeUpdate();
//	}

    @Override
    public ShiftChangeRequestDto saveOrUpdate(ShiftChangeRequestDto dto) {
        if (dto == null) return null;

        ShiftChangeRequest entity = (dto.getId() != null)
                ? repository.findById(dto.getId()).orElse(new ShiftChangeRequest())
                : null;

        // Kiểm tra xem đã có yêu cầu nào trước đó chưa dựa vào ca làm việc cũ, ngày làm việc và nhân viên
        if (entity == null
                && dto.getFromShiftWork() != null
                && dto.getFromShiftWork().getId() != null
                && dto.getRegisterStaff() != null
                && dto.getRegisterStaff().getId() != null
                && dto.getFromWorkingDate() != null) {
            List<Integer> approvalStatus = Arrays.asList(HrConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED_YET.getValue(), HrConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED.getValue());
            List<ShiftChangeRequest> existingRequests = repository.findByShiftWorkAndDateAndStaffAndStatus(
                    dto.getFromShiftWork().getId(),
                    dto.getFromWorkingDate(),
                    dto.getRegisterStaff().getId(),
                    approvalStatus);
            if (existingRequests != null && !existingRequests.isEmpty()) {
                entity = existingRequests.get(0);
            }
        }

        if (entity == null) {
            entity = new ShiftChangeRequest();
        }

        entity.setRegisterStaff(dto.getRegisterStaff() != null && dto.getRegisterStaff().getId() != null
                ? staffRepository.findById(dto.getRegisterStaff().getId()).orElse(null)
                : null);
        entity.setFromShiftWork(dto.getFromShiftWork() != null && dto.getFromShiftWork().getId() != null
                ? shiftWorkRepository.findById(dto.getFromShiftWork().getId()).orElse(null)
                : null);
        entity.setToShiftWork(dto.getToShiftWork() != null && dto.getToShiftWork().getId() != null
                ? shiftWorkRepository.findById(dto.getToShiftWork().getId()).orElse(null)
                : null);

        entity.setFromWorkingDate(dto.getFromWorkingDate());
        entity.setToWorkingDate(dto.getToWorkingDate());
        entity.setRequestDate(dto.getRequestDate());
        entity.setRequestReason(dto.getRequestReason());
        entity.setApprovalStatus(dto.getApprovalStatus());

        // Xử lý approvalStaff
        if (entity.getApprovalStatus() != null &&
                !entity.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.getValue())) {
            StaffDto currentStaff = userExtService.getCurrentStaff();
            if (currentStaff != null && currentStaff.getId() != null) {
                entity.setApprovalStaff(staffRepository.findById(currentStaff.getId()).orElse(null));
            }
        } else {
            entity.setApprovalStaff(null);
        }

        entity = repository.save(entity);
        return new ShiftChangeRequestDto(entity);
    }

    @Override
    public Page<ShiftChangeRequestDto> searchByPage(ShiftChangeRequestSearchDto dto) {
        if (dto == null) return null;

        int pageIndex = Math.max(dto.getPageIndex() - 1, 0);
        int pageSize = dto.getPageSize();

        // Kiểm tra role và gán staffId
        UserDto user = userExtService.getCurrentUser();
        boolean isManager = user != null && user.getRoles() != null &&
                user.getRoles().stream().anyMatch(role ->
                        "ROLE_ADMIN".equals(role.getName()) || "HR_MANAGER".equals(role.getName()));
        if (!isManager && dto.getStaffId() == null) {
            StaffDto staff = userExtService.getCurrentStaff();
            if (staff != null) dto.setStaffId(staff.getId());
        }

        String sql = "SELECT NEW com.globits.timesheet.dto.ShiftChangeRequestDto(entity) FROM ShiftChangeRequest entity ";
        String sqlCount = "SELECT COUNT(entity.id) FROM ShiftChangeRequest entity ";
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();
        boolean hasJoinMainPosition = false;
        String joinPositionStaff = "";
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.registerStaff.id ";
            hasJoinMainPosition = true;
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            where.append(" AND (entity.registerStaff.displayName LIKE :text OR entity.registerStaff.staffCode LIKE :text)");
            params.put("text", "%" + dto.getKeyword() + "%");
        }
        if (dto.getStaffId() != null) {
            where.append(" AND entity.registerStaff.id = :staffId");
            params.put("staffId", dto.getStaffId());
        }
        if (dto.getApprovalStaffId() != null) {
            where.append(" AND entity.approvalStaff.id = :approvalStaffId");
            params.put("approvalStaffId", dto.getApprovalStaffId());
        }
        if (dto.getApprovalStatus() != null) {
            where.append(" AND entity.approvalStatus = :approvalStatus");
            params.put("approvalStatus", dto.getApprovalStatus());
        }
        if (dto.getFromDate() != null) {
            where.append(" AND entity.fromWorkingDate >= :fromDate");
            params.put("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            where.append(" AND entity.toWorkingDate <= :toDate");
            params.put("toDate", dto.getToDate());
        }
        if (dto.getFromRequestDate() != null) {
            where.append(" AND entity.requestDate >= :fromRequestDate");
            params.put("fromRequestDate", dto.getFromRequestDate());
        }
        if (dto.getToRequestDate() != null) {
            where.append(" AND entity.requestDate <= :toRequestDate");
            params.put("toRequestDate", dto.getToRequestDate());
        }
        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                where.append(" AND pos.department.organization.id = :organizationId ");
                params.put("organizationId", dto.getOrganizationId());
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                where.append(" AND pos.department.id = :departmentId ");
                params.put("departmentId", dto.getDepartmentId());
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                where.append(" AND pos.title.id = :positionTitleId ");
                params.put("positionTitleId", dto.getPositionTitleId());
            }
        }

        sql += joinPositionStaff + where + " ORDER BY entity.modifyDate DESC";
        sqlCount += joinPositionStaff + where;

        Query q = manager.createQuery(sql, ShiftChangeRequestDto.class);
        Query qCount = manager.createQuery(sqlCount);
        params.forEach(q::setParameter);
        params.forEach(qCount::setParameter);

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        List<ShiftChangeRequestDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    @Modifying
// @Transactional
    public List<UUID> updateApprovalStatus(ShiftChangeRequestSearchDto dto) {
        if (dto == null || dto.getApprovalStatus() == null || dto.getChosenIds() == null || dto.getChosenIds().isEmpty()) {
            return Collections.emptyList();
        }

        // Lặp qua danh sách các yêu cầu đã chọn
        for (UUID requestId : dto.getChosenIds()) {
            ShiftChangeRequest request = repository.findById(requestId).orElse(null);

            if (request == null) {
                continue;
            }

            Integer oldStatus = request.getApprovalStatus();
            Integer newStatus = dto.getApprovalStatus();

            // Nếu trạng thái cũ chưa duyệt => duyệt thì tạo ca mới và xóa ca cũ
            if (!Objects.equals(oldStatus, HrConstants.ShiftChangeRequestApprovalStatus.APPROVED.getValue())
                    && Objects.equals(newStatus, HrConstants.ShiftChangeRequestApprovalStatus.APPROVED.getValue())) {
                approveChangeShift(request);
            }

            // Nếu trạng thái cũ đã duyệt => hủy hoặc chuyển trạng thái khác thì phục hồi ca cũ
            if (Objects.equals(oldStatus, HrConstants.ShiftChangeRequestApprovalStatus.APPROVED.getValue())
                    && !Objects.equals(newStatus, HrConstants.ShiftChangeRequestApprovalStatus.APPROVED.getValue())) {
                revertChangeShift(request);
            }

            // Cập nhật trạng thái mới
            request.setApprovalStatus(newStatus);
        }

        return dto.getChosenIds();
    }


    private void approveChangeShift(ShiftChangeRequest request) {
        // Cập nhật người phê duyệt
        Staff approvalStaff = userExtService.getCurrentStaffEntity();
        request.setApprovalStaff(approvalStaff);

        // Tìm ca làm việc cũ
        List<StaffWorkSchedule> oldSchedules = staffWorkScheduleRepository.getByStaffIdAndShiftWorkIdAndWorkingDate(
                request.getRegisterStaff().getId(), request.getFromShiftWork().getId(), request.getFromWorkingDate());

        StaffWorkSchedule oldSchedule = null;
        // Kiểm tra nếu có ca làm việc cũ
        if (oldSchedules != null && !oldSchedules.isEmpty()) {
            oldSchedule = oldSchedules.get(0);
        }

        // Tìm ca làm việc mới
        List<StaffWorkSchedule> newSchedules = staffWorkScheduleRepository.getByStaffIdAndShiftWorkIdAndWorkingDate(
                request.getRegisterStaff().getId(), request.getToShiftWork().getId(), request.getToWorkingDate());

        if (newSchedules == null || newSchedules.isEmpty()) {
            // Sao chép các trường đơn từ ca cũ
            StaffWorkSchedule newSchedule = new StaffWorkSchedule();
            newSchedule.setStaff(request.getRegisterStaff()); // Lấy từ yêu cầu đổi ca
            newSchedule.setShiftWork(request.getToShiftWork()); // Ca làm việc mới
            newSchedule.setWorkingDate(request.getToWorkingDate()); // Ngày làm việc mới
            newSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());
            if (oldSchedule != null) {
                // Gọi hàm sao chép các thuộc tính đơn
                copySimpleFieldsOfOldSchedule(oldSchedule, newSchedule);
                newSchedule.setApprovalStatus(HrConstants.ShiftChangeRequestApprovalStatus.APPROVED.getValue());
            } else {
                newSchedule.setTimekeepingCalculationType(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue());
                newSchedule.setAllowOneEntryOnly(true);
            }

            // Lưu ca làm việc mới
            staffWorkScheduleRepository.saveAndFlush(newSchedule);
        }

        if (oldSchedules != null && !oldSchedules.isEmpty()) {
            // Xóa các ca làm việc cũ
            staffWorkScheduleRepository.deleteAll(oldSchedules);
        }
    }

    private void revertChangeShift(ShiftChangeRequest request) {
        request.setApprovalStaff(null);

        // Tìm ca làm việc mới
        List<StaffWorkSchedule> newSchedules = staffWorkScheduleRepository.getByStaffIdAndShiftWorkIdAndWorkingDate(
                request.getRegisterStaff().getId(), request.getToShiftWork().getId(), request.getToWorkingDate());

        StaffWorkSchedule newSchedule = null;
        // Kiểm tra nếu có ca làm việc mới
        if (newSchedules != null && !newSchedules.isEmpty()) {
            newSchedule = newSchedules.get(0);
        }

        // Tìm ca làm việc cũ
        List<StaffWorkSchedule> originalSchedules = staffWorkScheduleRepository.getByStaffIdAndShiftWorkIdAndWorkingDate(
                request.getRegisterStaff().getId(), request.getFromShiftWork().getId(), request.getFromWorkingDate());

        if (originalSchedules == null || originalSchedules.isEmpty()) {
            // Tạo lại ca làm việc ban đầu
            StaffWorkSchedule originalSchedule = new StaffWorkSchedule();
            originalSchedule.setStaff(request.getRegisterStaff()); // Nhân viên từ yêu cầu
            originalSchedule.setShiftWork(request.getFromShiftWork()); // Ca làm việc cũ
            originalSchedule.setWorkingDate(request.getFromWorkingDate()); // Ngày làm việc cũ
            originalSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());

            if (newSchedule != null) {
                // Gọi hàm sao chép các thuộc tính đơn
                copySimpleFieldsOfOldSchedule(newSchedule, originalSchedule);
                newSchedule.setApprovalStatus(HrConstants.ShiftChangeRequestApprovalStatus.APPROVED.getValue());
            } else {
                originalSchedule.setTimekeepingCalculationType(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue());
                originalSchedule.setAllowOneEntryOnly(true);
            }

            // Lưu lại ca làm việc ban đầu
            staffWorkScheduleRepository.saveAndFlush(originalSchedule);
        }

        if (newSchedules != null && !newSchedules.isEmpty()) {
            // Xóa ca làm việc đã đổi
            staffWorkScheduleRepository.deleteAll(newSchedules);
        }
    }

    // Tạo một hàm riêng để sao chép các thuộc tính đơn từ ca cũ sang ca mới và cập nhật nhân viên phân ca
    private void copySimpleFieldsOfOldSchedule(StaffWorkSchedule fromSchedule, StaffWorkSchedule toSchedule) {
        toSchedule.setTotalHours(0D);
//        toSchedule.setWorkingType(fromSchedule.getWorkingType());
        toSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());
//        toSchedule.setPaidWorkStatus(fromSchedule.getPaidWorkStatus());
        toSchedule.setLateArrivalCount(0);
        toSchedule.setLateArrivalMinutes(0);
        toSchedule.setEarlyExitCount(0);
        toSchedule.setEarlyExitMinutes(0);
        toSchedule.setEarlyArrivalMinutes(0);
        toSchedule.setLateExitMinutes(0);
        toSchedule.setTotalPaidWork(0D);
        toSchedule.setAllowOneEntryOnly(fromSchedule.getAllowOneEntryOnly());
        toSchedule.setConvertedWorkingHours(0D);
        toSchedule.setOtEndorser(null);
        toSchedule.setConfirmedOTHoursBeforeShift(0D);
        toSchedule.setConfirmedOTHoursAfterShift(0D);
        toSchedule.setLeaveType(fromSchedule.getLeaveType());
        toSchedule.setTimekeepingCalculationType(fromSchedule.getTimekeepingCalculationType());
        toSchedule.setNeedManagerApproval(fromSchedule.getNeedManagerApproval());
        // Cập nhật nhân viên phân ca
        Staff coordinator = userExtService.getCurrentStaffEntity();
        toSchedule.setCoordinator(coordinator);
    }


}