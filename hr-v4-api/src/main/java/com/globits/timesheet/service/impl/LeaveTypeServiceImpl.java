package com.globits.timesheet.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Bank;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchLeaveTypeDto;
import com.globits.hr.repository.BankRepository;
import com.globits.hr.service.BankService;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.dto.LeaveTypeDto;
import com.globits.timesheet.repository.LeaveRequestRepository;
import com.globits.timesheet.repository.LeaveTypeRepository;
import com.globits.timesheet.service.LeaveTypeService;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveTypeServiceImpl extends GenericServiceImpl<LeaveType, UUID> implements LeaveTypeService {
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;


    @Override
    public Boolean isValidCode(LeaveTypeDto dto) {
        if (dto == null)
            return false;

        // ID of LeaveType is null => Create new LeaveType
        // => Assure that there's no other LeaveTypes using this code of new LeaveType
        // if there was any LeaveType using new LeaveType code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<LeaveType> entities = leaveTypeRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        }
        // ID of LeaveType is NOT null => LeaveType is modified
        // => Assure that the modified code is not same to OTHER any LeaveType's code
        // if there was any LeaveType using new LeaveType code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<LeaveType> entities = leaveTypeRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (LeaveType entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }


    @Override
    public LeaveTypeDto getById(UUID id) {
        if (id == null) return null;

        LeaveType entity = leaveTypeRepository.findById(id).orElse(null);

        return new LeaveTypeDto(entity);
    }

    @Override
    public LeaveTypeDto saveOrUpdate(LeaveTypeDto dto) {
        if (dto == null) return null;

        LeaveType entity = null;
        if (dto.getId() != null) {
            entity = leaveTypeRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null && dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
            List<LeaveType> availableLeaveTypes = leaveTypeRepository.findByCode(dto.getCode());
            if (availableLeaveTypes != null && !availableLeaveTypes.isEmpty()) {
                entity = availableLeaveTypes.get(0);
            }
        }
        if (entity == null) {
            entity = new LeaveType();
        }
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setIsPaid(dto.getIsPaid());
        entity.setUsedForRequest(dto.getUsedForRequest());

        LeaveType response = leaveTypeRepository.save(entity);

        return new LeaveTypeDto(response);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) return null;

        LeaveType entity = leaveTypeRepository.findById(id).orElse(null);

        if (entity == null) {
            return false;
        }

        leaveTypeRepository.delete(entity);
        return true;
    }

    @Override
    @Transactional
    public Integer deleteMultiple(List<UUID> ids) {
        int result = 0;
        for (UUID id : ids) {
            deleteById(id);
            result++;
        }
        return result;
    }

    @Override
    public Page<LeaveTypeDto> searchByPage(SearchLeaveTypeDto dto) {
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

        String whereClause = "";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(entity.id) from LeaveType as entity where (1=1) ";
        String sql = "select new  com.globits.timesheet.dto.LeaveTypeDto(entity) from LeaveType as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
        }
        if (dto.getIsPaid() != null) {
            whereClause += " and entity.isPaid = :isPaid ";
        }
        if (dto.getUsedForRequest() != null) {
            whereClause += " and entity.usedForRequest = :usedForRequest ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, BankDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getIsPaid() != null) {
            q.setParameter("isPaid", dto.getIsPaid());
            qCount.setParameter("isPaid", dto.getIsPaid());
        }
        if (dto.getUsedForRequest() != null) {
            q.setParameter("usedForRequest", dto.getUsedForRequest());
            qCount.setParameter("usedForRequest", dto.getUsedForRequest());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<LeaveTypeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public List<LeaveTypeDto> getListLeaveTypeDto() {
        try {
            return leaveTypeRepository.getListLeaveTypeDto();
        } catch (Exception e) {
            return null;
        }
    }


    // Lấy loại nghỉ nửa ngày của loại nghỉ tương ứng
    @Override
    public LeaveType getHalfLeaveOfLeaveType(LeaveType leaveType) {
        if (leaveType == null) return null;

        //nghỉ phép
        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_ANNUAL_LEAVE.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.HALF_ANNUAL_LEAVE.getCode());
        }
        // nghỉ công tác
        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_BUSINESS_TRIP.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.HALF_BUSINESS_TRIP.getCode());
        }
        //Nghỉ lễ
        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_PUBLIC_HOLIDAY.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.HALF_PUBLIC_HOLIDAY.getCode());
        }
        //Nghỉ bù
        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_COMPENSATORY_LEAVE.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.HALF_COMPENSATORY_LEAVE.getCode());
        }
        //Nghỉ chế độ
        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_SPECIAL_LEAVE.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.HALF_SPECIAL_LEAVE.getCode());
        }
        //Nghỉ không lương
        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_UNPAID_LEAVE.getCode())
        		|| leaveType.getCode().equals(HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode())) {
        	return this.findOneEntityByCode(HrConstants.LeaveTypeCode.HALF_UNPAID_LEAVE.getCode());
        }
        //Nghỉ nmă
        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.YEAR_LEAVE.getCode())
        		|| leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_YEAR_LEAVE.getCode())) {
        	return this.findOneEntityByCode(HrConstants.LeaveTypeCode.HALF_YEAR_LEAVE.getCode());
        }

        return null;
    }

    // Lấy loại nghỉ cả ngày của loại nghỉ tương ứng
    @Override
    public LeaveType getFullLeaveOfLeaveType(LeaveType leaveType) {
        if (leaveType == null) return null;

        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_ANNUAL_LEAVE.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode());
        }

        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_BUSINESS_TRIP.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode());
        }

        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_PUBLIC_HOLIDAY.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode());
        }

        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_COMPENSATORY_LEAVE.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode());
        }

        if (leaveType.getCode().equals(HrConstants.LeaveTypeCode.HALF_SPECIAL_LEAVE.getCode())
                || leaveType.getCode().equals(HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode())) {
            return this.findOneEntityByCode(HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode());
        }

        return null;
    }


    // Đặt trạng thái tính nghỉ của ca làm việc
    @Override
    public void handleSetLeaveTypeForStaffWorkSchedule(StaffWorkSchedule entity) {
        if (entity == null || entity.getStaff() == null || entity.getWorkingDate() == null) return;

        List<LeaveRequest> availableRequests = leaveRequestRepository.findByStaffIdWorkingDateAndApprovalStatus(entity.getStaff().getId(),
                entity.getWorkingDate(),
                HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue());

        // Nếu không có yêu cầu nghỉ phép nào
        // => Nếu nghỉ trong ca làm việc này thì tính nghỉ không lương
        if (availableRequests == null || availableRequests.isEmpty()) {
            this.setLeaveWithoutPaidType(entity);
        }
        // Nếu đã có yêu cầu nghỉ phép
        // => Cập nhật cách tính nghỉ của ca làm việc nếu ca làm việc nằm trong yêu cầu nghỉ
        else {
            // Kiểm tra yêu cầu nghỉ có chứa khoảng thời gian làm việc hay không
            LeaveRequest leaveRequest = availableRequests.get(0);

            // Ca làm việc có chứa khoảng thời gian xin nghỉ hay không
            boolean isIntersected = hasIntersection(leaveRequest, entity);

            // Yêu cầu nghỉ có giao vào thời gian làm việc của ca
            // => Khi nghỉ tính loại nghỉ của yêu cầu nghỉ phép
            if (isIntersected) {
                LeaveType leaveType = leaveRequest.getLeaveType();
                entity.setLeaveType(leaveType);
            }
            // Nếu yêu cầu nghỉ không giao với thời gian làm việc của ca
            // => Nếu nhân viên nghỉ thì tính là nghỉ không lương
            else {
                this.setLeaveWithoutPaidType(entity);
            }

        }
    }

    // Đặt trạng thái nghỉ là nghỉ không lương
    private void setLeaveWithoutPaidType(StaffWorkSchedule entity) {
        List<LeaveType> availableLeaveTypes = leaveTypeRepository.findByCode(HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode());
        if (availableLeaveTypes == null || availableLeaveTypes.isEmpty()) return;
        entity.setLeaveType(availableLeaveTypes.get(0));
    }

    private boolean hasIntersection(LeaveRequest leaveRequest, StaffWorkSchedule staffWorkSchedule) {
        if (leaveRequest == null || staffWorkSchedule == null || staffWorkSchedule.getShiftWork() == null
                || staffWorkSchedule.getWorkingDate() == null) {
            return false;
        }

        Date workingDate = staffWorkSchedule.getWorkingDate();
        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();

        Date leaveFrom = leaveRequest.getFromDate();
        Date leaveTo = leaveRequest.getToDate();

        if (leaveFrom == null || leaveTo == null) return false;

        for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
            // Lấy giờ-phút của start & end
            LocalTime periodStartTime = toLocalTime(period.getStartTime());
            LocalTime periodEndTime = toLocalTime(period.getEndTime());

            // Ghép vào ngày làm việc
            LocalDate workDay = toLocalDate(workingDate);
            LocalDateTime shiftStart = LocalDateTime.of(workDay, periodStartTime);
            LocalDateTime shiftEnd = LocalDateTime.of(workDay, periodEndTime);

            // So sánh giao với khoảng nghỉ
            LocalDateTime leaveStart = toLocalDateTime(leaveFrom);
            LocalDateTime leaveEnd = toLocalDateTime(leaveTo);

            boolean intersect = leaveStart.isBefore(shiftEnd) && leaveEnd.isAfter(shiftStart);
            if (intersect) {
                return true;
            }
        }

        return false;
    }

    // Helper convert Date → LocalTime
    private LocalTime toLocalTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    // Helper convert Date → LocalDate
    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Helper convert Date → LocalDateTime
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


    @Override
    public LeaveTypeDto findOneByCode(String code) {
        if (code == null || !StringUtils.hasText(code)) return null;

        LeaveType leaveType = this.findOneEntityByCode(code);

        if (leaveType == null) return null;

        return new LeaveTypeDto(leaveType);
    }

    private LeaveType findOneEntityByCode(String code) {
        if (code == null || !StringUtils.hasText(code)) return null;

        List<LeaveType> availableLeaveTypes = leaveTypeRepository.findByCode(code);
        if (availableLeaveTypes == null || availableLeaveTypes.isEmpty()) return null;

        return (availableLeaveTypes.get(0));
    }
}
