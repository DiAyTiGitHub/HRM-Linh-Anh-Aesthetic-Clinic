package com.globits.timesheet.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.*;
import com.globits.hr.dto.ShiftWorkDto;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

// Yêu cầu nghỉ làm việc trong 1 khoảng thời gian
@Table(name = "tbl_leave_request")
@Entity
public class LeaveRequest extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_staff_id")
    private Staff requestStaff; // nhân viên yêu cầu nghỉ

    @Column(name = "request_date")
    private Date requestDate; // ngày tạo yêu cầu nghỉ

    @Column(name = "from_date")
    private Date fromDate; // Thời gian bắt đầu nghỉ

    @Column(name = "from_date_leave_type")
    private Integer fromDateLeaveType; // Loại nghỉ tại ngày bắt đầu nghỉ. Chi tiết: HrConstants.LeaveShiftType

    @Column(name = "to_date")
    private Date toDate; // Thời gian kết thúc nghỉ

    @Column(name = "to_date_leave_type")
    private Integer toDateLeaveType; // Loại nghỉ tại ngày cuối cùng nghỉ. Chi tiết: HrConstants.LeaveShiftType

    @Column(name = "total_days")
    private Double totalDays;

    @Column(name = "total_hours")
    private Double totalHours;

    @Column(name = "request_reason", length = 1000)
    private String requestReason; // Lý do yêu cầu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType; // Loại nghỉ

    @Column(name = "approval_status")
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.AbsenceRequestApprovalStatus

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_staff_id")
    private Staff approvalStaff; // nhân viên phê duyệt
    @Column(name = "half_day_leave")
    private Boolean halfDayLeave = false;
    @Column(name = "half_day_leave_start")
    private Boolean halfDayLeaveStart = false;
    @Column(name = "half_day_leave_end")
    private Boolean halfDayLeaveEnd = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_work_start")
    private ShiftWork shiftWorkStart;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_work_end")
    private ShiftWork shiftWorkEnd;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_period_start")
    private ShiftWorkTimePeriod timePeriodStart;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_period_end")
    private ShiftWorkTimePeriod timePeriodEnd;

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

    public Staff getRequestStaff() {
        return requestStaff;
    }

    public void setRequestStaff(Staff requestStaff) {
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

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Staff getApprovalStaff() {
        return approvalStaff;
    }

    public void setApprovalStaff(Staff approvalStaff) {
        this.approvalStaff = approvalStaff;
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

    public ShiftWork getShiftWorkStart() {
        return shiftWorkStart;
    }

    public void setShiftWorkStart(ShiftWork shiftWorkStart) {
        this.shiftWorkStart = shiftWorkStart;
    }

    public ShiftWork getShiftWorkEnd() {
        return shiftWorkEnd;
    }

    public void setShiftWorkEnd(ShiftWork shiftWorkEnd) {
        this.shiftWorkEnd = shiftWorkEnd;
    }

    public ShiftWorkTimePeriod getTimePeriodStart() {
        return timePeriodStart;
    }

    public void setTimePeriodStart(ShiftWorkTimePeriod timePeriodStart) {
        this.timePeriodStart = timePeriodStart;
    }

    public ShiftWorkTimePeriod getTimePeriodEnd() {
        return timePeriodEnd;
    }

    public void setTimePeriodEnd(ShiftWorkTimePeriod timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;
    }
}
