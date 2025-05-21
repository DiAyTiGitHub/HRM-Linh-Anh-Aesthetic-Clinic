package com.globits.timesheet.dto;

import java.util.Date;
import java.util.UUID;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.LeaveRequest;
import jakarta.persistence.Column;

public class LeaveRequestDto extends BaseObjectDto {

    private StaffDto requestStaff;
    private Date requestDate;
    private Date fromDate;
    private Integer fromDateLeaveType; // Loại nghỉ tại ngày bắt đầu nghỉ. Chi tiết: HrConstants.LeaveShiftType
    private Date toDate;
    private Integer toDateLeaveType; // Loại nghỉ tại ngày cuối cùng nghỉ. Chi tiết: HrConstants.LeaveShiftType
    private Double totalDays;
    private Double totalHours;
    private String requestReason;
    private LeaveTypeDto leaveType;
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.AbsenceRequestApprovalStatus
    private StaffDto approvalStaff; // nhân viên phê duyệt
    private Boolean halfDayLeave = false;
    private Boolean halfDayLeaveStart = false;
    private Boolean halfDayLeaveEnd = false;
    private ShiftWorkDto shiftWorkStart;
    private ShiftWorkDto shiftWorkEnd;
    private ShiftWorkTimePeriodDto timePeriodStart;
    private ShiftWorkTimePeriodDto timePeriodEnd;
    private String errorMessage;

    public LeaveRequestDto() {

    }

    public LeaveRequestDto(LeaveRequest entity) {
        super(entity);
        if (entity == null) return;

        if (entity.getRequestStaff() != null) {
            this.requestStaff = new StaffDto();
            this.requestStaff.setId(entity.getRequestStaff().getId());
            this.requestStaff.setDisplayName(entity.getRequestStaff().getDisplayName());
            this.requestStaff.setStaffCode(entity.getRequestStaff().getStaffCode());
            this.requestStaff.setMainPosition(entity.getRequestStaff().getCurrentPositions());
            this.requestStaff.setStartDate(entity.getRequestStaff().getStartDate());
        }

        this.requestDate = entity.getRequestDate();
        this.fromDate = entity.getFromDate();
        this.fromDateLeaveType = entity.getFromDateLeaveType();
        this.toDate = entity.getToDate();
        this.toDateLeaveType = entity.getToDateLeaveType();
        this.totalDays = entity.getTotalDays();
        this.totalHours = entity.getTotalHours();
        this.requestReason = entity.getRequestReason();

        if (entity.getLeaveType() != null) {
            this.leaveType = new LeaveTypeDto();
            this.leaveType.setId(entity.getLeaveType().getId());
            this.leaveType.setName(entity.getLeaveType().getName());
            this.leaveType.setCode(entity.getLeaveType().getCode());
            this.leaveType.setIsPaid(entity.getLeaveType().getIsPaid());
        }

        this.approvalStatus = entity.getApprovalStatus();

        if (entity.getApprovalStaff() != null) {
            this.approvalStaff = new StaffDto();
            this.approvalStaff.setId(entity.getApprovalStaff().getId());
            this.approvalStaff.setDisplayName(entity.getApprovalStaff().getDisplayName());
            this.approvalStaff.setStaffCode(entity.getApprovalStaff().getStaffCode());
        }
        this.halfDayLeave = entity.getHalfDayLeave();
        this.halfDayLeaveStart = entity.getHalfDayLeaveStart();
        this.halfDayLeaveEnd = entity.getHalfDayLeaveEnd();
        if (entity.getShiftWorkStart() != null) {
            this.shiftWorkStart = new ShiftWorkDto();
            this.shiftWorkStart.setId(entity.getShiftWorkStart().getId());
            this.shiftWorkStart.setName(entity.getShiftWorkStart().getName());
            if (entity.getShiftWorkStart().getTimePeriods() != null) {
                this.shiftWorkStart.getTimePeriods().addAll(entity.getShiftWorkStart().getTimePeriods().stream().map(ShiftWorkTimePeriodDto::new).toList());
            }
        }
        if (entity.getShiftWorkEnd() != null) {
            this.shiftWorkEnd = new ShiftWorkDto();
            this.shiftWorkEnd.setId(entity.getShiftWorkEnd().getId());
            this.shiftWorkEnd.setName(entity.getShiftWorkEnd().getName());
            if (entity.getShiftWorkEnd().getTimePeriods() != null) {
                this.shiftWorkEnd.getTimePeriods().addAll(entity.getShiftWorkEnd().getTimePeriods().stream().map(ShiftWorkTimePeriodDto::new).toList());
            }
        }
        if (entity.getTimePeriodStart() != null) {
            this.timePeriodStart = new ShiftWorkTimePeriodDto(entity.getTimePeriodStart());
        }
        if (entity.getTimePeriodEnd() != null) {
            this.timePeriodEnd = new ShiftWorkTimePeriodDto(entity.getTimePeriodEnd());
        }
    }

    public Integer getFromDateLeaveType() {
        return fromDateLeaveType;
    }

    public void setFromDateLeaveType(Integer fromDateLeaveType) {
        this.fromDateLeaveType = fromDateLeaveType;
    }

    public Integer getToDateLeaveType() {
        return toDateLeaveType;
    }

    public void setToDateLeaveType(Integer toDateLeaveType) {
        this.toDateLeaveType = toDateLeaveType;
    }

    public StaffDto getRequestStaff() {
        return requestStaff;
    }

    public void setRequestStaff(StaffDto requestStaff) {
        this.requestStaff = requestStaff;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public LeaveTypeDto getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveTypeDto leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public StaffDto getApprovalStaff() {
        return approvalStaff;
    }

    public void setApprovalStaff(StaffDto approvalStaff) {
        this.approvalStaff = approvalStaff;
    }

    public Double getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Double totalDays) {
        this.totalDays = totalDays;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public Boolean getHalfDayLeave() {
        return halfDayLeave;
    }

    public void setHalfDayLeave(Boolean halfDayLeave) {
        this.halfDayLeave = halfDayLeave;
    }

    public Boolean getHalfDayLeaveStart() {
        return halfDayLeaveStart;
    }

    public void setHalfDayLeaveStart(Boolean halfDayLeaveStart) {
        this.halfDayLeaveStart = halfDayLeaveStart;
    }

    public Boolean getHalfDayLeaveEnd() {
        return halfDayLeaveEnd;
    }

    public void setHalfDayLeaveEnd(Boolean halfDayLeaveEnd) {
        this.halfDayLeaveEnd = halfDayLeaveEnd;
    }

    public ShiftWorkTimePeriodDto getTimePeriodStart() {
        return timePeriodStart;
    }

    public void setTimePeriodStart(ShiftWorkTimePeriodDto timePeriodStart) {
        this.timePeriodStart = timePeriodStart;
    }

    public ShiftWorkTimePeriodDto getTimePeriodEnd() {
        return timePeriodEnd;
    }

    public void setTimePeriodEnd(ShiftWorkTimePeriodDto timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;
    }

    public ShiftWorkDto getShiftWorkEnd() {
        return shiftWorkEnd;
    }

    public void setShiftWorkEnd(ShiftWorkDto shiftWorkEnd) {
        this.shiftWorkEnd = shiftWorkEnd;
    }

    public ShiftWorkDto getShiftWorkStart() {
        return shiftWorkStart;
    }

    public void setShiftWorkStart(ShiftWorkDto shiftWorkStart) {
        this.shiftWorkStart = shiftWorkStart;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
