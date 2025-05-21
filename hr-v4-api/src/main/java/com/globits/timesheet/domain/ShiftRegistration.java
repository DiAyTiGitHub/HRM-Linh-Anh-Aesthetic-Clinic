package com.globits.timesheet.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.data.types.TimeSheetRegStatus;
import com.globits.hr.domain.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

// Yêu cầu đăng ký ca làm việc
@Table(name = "tbl_shift_registration")
@Entity
public class ShiftRegistration extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "register_staff_id")
    private Staff registerStaff; // nhân viên đăng ký

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_work_id")
    private ShiftWork shiftWork; // ca làm việc đăng ký

    @Column(name = "working_date", nullable = true)
    private Date workingDate; // ngày làm việc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_staff_id")
    private Staff approvalStaff; // nhân viên phê duyệt

    @Column(name = "approval_status")
    private Integer approvalStatus; // Trạng thái phê duyệt yêu cầu đăng ký làm việc. Chi tiết: HrConstants.ShiftRegistrationApprovalStatus

    @Column(name = "working_type", nullable = true)
    private Integer workingType; // Loại làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingType

    @Column(name = "overtime_hours", nullable = true)
    private Double overtimeHours; // Thời gian đăng ký làm thêm. Có giá trị khi: workingType = EXTENDED_OVERTIME (Tăng ca kéo dài)



    public Staff getRegisterStaff() {
        return registerStaff;
    }

    public void setRegisterStaff(Staff registerStaff) {
        this.registerStaff = registerStaff;
    }

    public ShiftWork getShiftWork() {
        return shiftWork;
    }

    public void setShiftWork(ShiftWork shiftWork) {
        this.shiftWork = shiftWork;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public Staff getApprovalStaff() {
        return approvalStaff;
    }

    public void setApprovalStaff(Staff approvalStaff) {
        this.approvalStaff = approvalStaff;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getWorkingType() {
        return workingType;
    }

    public void setWorkingType(Integer workingType) {
        this.workingType = workingType;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
}
