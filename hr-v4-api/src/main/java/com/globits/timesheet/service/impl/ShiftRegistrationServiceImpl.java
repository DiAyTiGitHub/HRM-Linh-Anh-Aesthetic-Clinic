package com.globits.timesheet.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.hr.repository.ShiftWorkRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.UserExtService;
import com.globits.timesheet.domain.ShiftRegistration;
import com.globits.timesheet.dto.search.SearchShiftRegistrationDto;
import com.globits.timesheet.dto.ShiftRegistrationDto;
import com.globits.timesheet.repository.ShiftRegistrationRepository;
import com.globits.timesheet.service.ShiftRegistrationService;
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

import java.util.*;

@Service
public class ShiftRegistrationServiceImpl extends GenericServiceImpl<ShiftRegistration, UUID> implements ShiftRegistrationService {

    @Autowired
    ShiftRegistrationRepository repository;
    @Autowired
    private ShiftWorkRepository shiftWorkRepository;
    @Autowired
    private StaffService staffService;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Override
    public ShiftRegistrationDto getShiftRegistration(UUID id) {
        ShiftRegistration entity = this.getEntityById(id);
        if (entity == null)
            return null;
        return new ShiftRegistrationDto(entity);
    }

    @Override
    public ShiftRegistration getEntityById(UUID id) {
        ShiftRegistration entity = null;
        Optional<ShiftRegistration> optional = repository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        return entity;
    }

    @Override
    public Boolean deleteById(UUID id) {
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public ShiftRegistrationDto saveOrUpdate(ShiftRegistrationDto dto) {
        if (dto == null) {
            return null;
        }
        StaffDto staffDto = userExtService.getCurrentStaff();

        ShiftRegistration entity = null;
        if (dto.getId() != null) {
            entity = this.getEntityById(dto.getId());
        }

        if (entity == null) {
            entity = new ShiftRegistration();
        }

        entity.setWorkingDate(dto.getWorkingDate());

        if (dto.getRegisterStaff() != null && dto.getRegisterStaff().getId() != null) {
            Staff registerStaff = staffService.getEntityById(dto.getRegisterStaff().getId());
            entity.setRegisterStaff(registerStaff);
        } else if (staffDto.getId() != null) {
            Staff staff = staffService.getEntityById(staffDto.getId());
            entity.setRegisterStaff(staff);
        } else {
            entity.setRegisterStaff(null);
        }

        if (entity.getRegisterStaff() == null) return null;

//        if (dto.getApprovalStatus() != null) {
//            entity.setApprovalStatus(dto.getApprovalStatus());
//            if (!dto.getApprovalStatus().equals(HrConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.getValue())) {
//                if (staffDto.getId() != null) {
//                	Staff approvalStaff = staffService.getEntityById(staffDto.getId());
//                	entity.setApprovalStaff(approvalStaff);
//                }
//            } else {
//                entity.setApprovalStaff(null);
//            }
//            // TODO: tao Phan ca lam viec
//        } else {
//            entity.setApprovalStatus(null);
//        }

        if (dto.getApprovalStatus() != null) {
            entity.setApprovalStatus(dto.getApprovalStatus());
        } else {
            entity.setApprovalStatus(null);
        }

        if (dto.getApprovalStaff() != null && dto.getApprovalStaff().getId() != null) {
            Staff approvalStaff = staffService.getEntityById(dto.getApprovalStaff().getId());
            entity.setApprovalStaff(approvalStaff);
        } else {
            entity.setApprovalStaff(null);
        }

        if (dto.getShiftWork() != null && dto.getShiftWork().getId() != null) {
            ShiftWork shiftWork = shiftWorkRepository.findById(dto.getShiftWork().getId()).orElse(null);
            entity.setShiftWork(shiftWork);
        } else {
            entity.setShiftWork(null);
        }

        entity.setWorkingType(dto.getWorkingType());
        entity.setOvertimeHours(dto.getOvertimeHours());

        entity = repository.save(entity);
        return new ShiftRegistrationDto(entity);
    }

    @Override
    public Page<ShiftRegistrationDto> pagingShiftRegistrations(SearchShiftRegistrationDto dto) {
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
        String orderBy = "";
        String sql = "select new com.globits.timesheet.dto.ShiftRegistrationDto(entity) from ShiftRegistration entity ";
        String sqlCount = "select count(entity.id) from ShiftRegistration as entity ";
        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.registerStaff.id ";
            hasJoinMainPosition = true;
        }

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.registerStaff.displayName) LIKE UPPER(:text) ) OR ( UPPER(entity.registerStaff.staffCode) LIKE UPPER(:text) )";
        }
        if (dto.getRegisterStaff() != null && dto.getRegisterStaff().getId() != null) {
            whereClause += " and entity.registerStaff.id = :registerStaffId ";
        }
        if (dto.getShiftWork() != null && dto.getShiftWork().getId() != null) {
            whereClause += " and entity.shiftWork.id = :shiftWorkId ";
        }
        if (dto.getFromDate() != null && dto.getToDate() != null) {
            whereClause += " and DATE(entity.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) ";
        } else if (dto.getFromDate() != null) {
            whereClause += " and DATE(entity.workingDate) >= DATE(:fromDate) ";
        } else if (dto.getToDate() != null) {
            whereClause += " and DATE(entity.workingDate) <= DATE(:toDate) ";
        }
        whereClause += " and (entity.voided is null or entity.voided = false) ";

        if (dto.getStatus() != null) {
            if (dto.getStatus().equals(HrConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.getValue())) {
                whereClause += " and entity.approvalStatus = 1 ";
            } else if (dto.getStatus().equals(HrConstants.ShiftRegistrationApprovalStatus.APPROVED.getValue())) {
                whereClause += " and entity.approvalStatus = 2 ";
            } else if (dto.getStatus().equals(HrConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED.getValue())) {
                whereClause += " and entity.approvalStatus = 3 ";
            }
        }
        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                whereClause += " AND pos.department.organization.id = :organizationId ";
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                whereClause += " AND pos.department.id = :departmentId ";
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                whereClause += " AND pos.title.id = :positionTitleId ";
            }
        }

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;

        Query query = manager.createQuery(sql, ShiftRegistrationDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getRegisterStaff() != null && dto.getRegisterStaff().getId() != null) {
            query.setParameter("registerStaffId", dto.getRegisterStaff().getId());
            qCount.setParameter("registerStaffId", dto.getRegisterStaff().getId());
        }
        if (dto.getShiftWork() != null && dto.getShiftWork().getId() != null) {
            query.setParameter("shiftWorkId", dto.getShiftWork().getId());
            qCount.setParameter("shiftWorkId", dto.getShiftWork().getId());
        }
        if (dto.getWorkingDate() != null) {
            query.setParameter("workingDate", dto.getWorkingDate());
            qCount.setParameter("workingDate", dto.getWorkingDate());
        }
        if (dto.getApprovalStaff() != null && dto.getApprovalStaff().getId() != null) {
            query.setParameter("approvalStaffId", dto.getApprovalStaff().getId());
            qCount.setParameter("approvalStaffId", dto.getApprovalStaff().getId());
        }
        if (dto.getFromDate() != null && dto.getToDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
            query.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        } else if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        } else if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
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

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<ShiftRegistrationDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    @Transactional
    @Modifying
    public List<UUID> updateApprovalStatus(SearchShiftRegistrationDto searchDto) {
        if (searchDto == null || searchDto.getStatus() == null || searchDto.getChosenIds() == null || searchDto.getChosenIds().isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> updatedIds = new ArrayList<>();

        for (UUID id : searchDto.getChosenIds()) {
            ShiftRegistration registration = repository.findById(id).orElse(null);
            if (registration == null) continue;

            Integer oldStatus = registration.getApprovalStatus();
            Integer newStatus = searchDto.getStatus();

            // Nếu duyệt
            if (!Objects.equals(oldStatus, HrConstants.ShiftRegistrationApprovalStatus.APPROVED.getValue())
                    && Objects.equals(newStatus, HrConstants.ShiftRegistrationApprovalStatus.APPROVED.getValue())) {
                approveShiftRegistration(registration);
            }

            // Nếu hủy duyệt
            if (Objects.equals(oldStatus, HrConstants.ShiftRegistrationApprovalStatus.APPROVED.getValue())
                    && !Objects.equals(newStatus, HrConstants.ShiftRegistrationApprovalStatus.APPROVED.getValue())) {
                revertShiftRegistration(registration);
            }

            // Cập nhật trạng thái mới
            registration.setApprovalStatus(newStatus);
            updatedIds.add(id);
        }

        return updatedIds;
    }

    private void approveShiftRegistration(ShiftRegistration registration) {
        Staff approvalStaff = userExtService.getCurrentStaffEntity();
        registration.setApprovalStaff(approvalStaff);

        List<StaffWorkSchedule> schedules = staffWorkScheduleRepository.getByStaffIdAndShiftWorkIdAndWorkingDate(
                registration.getRegisterStaff().getId(),
                registration.getShiftWork().getId(),
                registration.getWorkingDate()
        );

        StaffWorkSchedule schedule = null;
        if (schedules != null && !schedules.isEmpty()) {
            schedule = schedules.get(0);
        }
        if (schedule == null) {
            schedule = new StaffWorkSchedule();
            schedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());
        }

        schedule.setStaff(registration.getRegisterStaff());
        schedule.setShiftWork(registration.getShiftWork());
        schedule.setWorkingDate(registration.getWorkingDate());
        schedule.setCoordinator(approvalStaff);
        schedule.setAllowOneEntryOnly(true);
        schedule.setTimekeepingCalculationType(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue());

        staffWorkScheduleRepository.save(schedule);
    }

    private void revertShiftRegistration(ShiftRegistration registration) {
        registration.setApprovalStaff(null);

        List<StaffWorkSchedule> schedules = staffWorkScheduleRepository.getByStaffIdAndShiftWorkIdAndWorkingDate(
                registration.getRegisterStaff().getId(),
                registration.getShiftWork().getId(),
                registration.getWorkingDate()
        );

        if (schedules != null && !schedules.isEmpty()) {
            staffWorkScheduleRepository.deleteAll(schedules);
        }
    }


    @Override
    public StaffWorkScheduleDto createStaffWorkSchedule(ShiftRegistrationDto shiftRegistrationDto) {
        if (shiftRegistrationDto == null || shiftRegistrationDto.getShiftWork() == null) {
            return null;
        }

        ShiftRegistration onCreateEntity = repository.findById(shiftRegistrationDto.getId()).orElse(null);
        if (onCreateEntity == null || onCreateEntity.getRegisterStaff() == null) return null;

        List<StaffWorkSchedule> availableRecords = staffWorkScheduleRepository.getByStaffIdAndShiftWorkIdAndWorkingDate(onCreateEntity.getRegisterStaff().getId(), onCreateEntity.getShiftWork().getId(), onCreateEntity.getWorkingDate());

        // schedule had been created
        if (availableRecords != null && !availableRecords.isEmpty())
            return new StaffWorkScheduleDto(availableRecords.get(0));


        // create new working schedule when it was not created
        StaffWorkSchedule staffWorkSchedule = new StaffWorkSchedule();

        Staff staff = staffService.findById(onCreateEntity.getRegisterStaff().getId());
        if (staff == null) return null;

        staffWorkSchedule.setStaff(staff);

        ShiftWork shiftWork = shiftWorkRepository.findById(onCreateEntity.getShiftWork().getId()).orElse(null);

        if (shiftWork == null) return null;

        staffWorkSchedule.setShiftWork(shiftWork);
        staffWorkSchedule.setWorkingDate(onCreateEntity.getWorkingDate());
//        staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());

        StaffWorkSchedule savedEntity = staffWorkScheduleRepository.save(staffWorkSchedule);

        return new StaffWorkScheduleDto(savedEntity);
    }

    @Override
    public String createStaffWorkSchedules(List<ShiftRegistrationDto> listShiftRegistrationDto) {
        if (listShiftRegistrationDto == null || listShiftRegistrationDto.isEmpty()) {
            return "No shift registrations provided.";
        }

        for (ShiftRegistrationDto shiftRegistrationDto : listShiftRegistrationDto) {
            createStaffWorkSchedule(shiftRegistrationDto);
        }

        return "Staff work schedules created successfully." + listShiftRegistrationDto.toArray().length;
    }

    @Override
    public int markDelete(UUID id) {
        Query q = manager.createQuery("update ShiftRegistration s set s.voided=true where s.id=:id");
        q.setParameter("id", id);
        return q.executeUpdate();
    }
}
