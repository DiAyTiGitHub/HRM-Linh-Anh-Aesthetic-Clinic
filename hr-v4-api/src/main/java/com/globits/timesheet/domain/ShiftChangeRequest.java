package com.globits.timesheet.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.Staff;
import jakarta.persistence.*;

import java.util.Date;

// Yêu cầu Đổi ca làm việc
@Table(name = "tbl_shift_change_request")
@Entity
public class ShiftChangeRequest extends BaseObject {
    private static final long serialVersionUID = 1L;

    // CA LÀM VIỆC CẦN THAY ĐỔI
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_shift_work_id")
    private ShiftWork fromShiftWork; // ca làm việc cần thay đổi

    @Column(name = "from_working_date", nullable = true)
    private Date fromWorkingDate; // ngày làm việc cần thay đổi

    // CA LÀM VIỆC ĐƯỢC YÊU CẦU ĐỔI
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_shift_work_id")
    private ShiftWork toShiftWork; // ca làm việc được yêu cầu đổi

    @Column(name = "to_working_date", nullable = true)
    private Date toWorkingDate; // ngày làm việc được yêu cầu đổi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "register_staff_id")
    private Staff registerStaff; // nhân viên yêu cầu đổi ca

    @Column(name = "request_date")
    private Date requestDate; // ngày tạo yêu cầu đổi ca

    @Column(name = "request_reason", length = 1000)
    private String requestReason; // Lý do yêu cầu

    @Column(name = "approval_status")
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.ShiftChangeRequestApprovalStatus

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_staff_id")
    private Staff approvalStaff; // nhân viên phê duyệt


    public ShiftWork getFromShiftWork() {
        return fromShiftWork;
    }

    public void setFromShiftWork(ShiftWork fromShiftWork) {
        this.fromShiftWork = fromShiftWork;
    }

    public Date getFromWorkingDate() {
        return fromWorkingDate;
    }

    public void setFromWorkingDate(Date fromWorkingDate) {
        this.fromWorkingDate = fromWorkingDate;
    }

    public ShiftWork getToShiftWork() {
        return toShiftWork;
    }

    public void setToShiftWork(ShiftWork toShiftWork) {
        this.toShiftWork = toShiftWork;
    }

    public Date getToWorkingDate() {
        return toWorkingDate;
    }

    public void setToWorkingDate(Date toWorkingDate) {
        this.toWorkingDate = toWorkingDate;
    }

    public Staff getRegisterStaff() {
        return registerStaff;
    }

    public void setRegisterStaff(Staff registerStaff) {
        this.registerStaff = registerStaff;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
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
}
