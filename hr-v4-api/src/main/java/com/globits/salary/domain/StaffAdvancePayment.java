package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Staff;
import jakarta.persistence.*;

import java.util.Date;

// Tạm ứng tiền lương nhân vien
@Table(name = "tbl_staff_advance_payment")
@Entity
public class StaffAdvancePayment extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff; // Nhân viên xin ứng truước

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_period_id")
    private SalaryPeriod salaryPeriod; // Kỳ lương xin ứng

    @Column(name = "request_date")
    private Date requestDate; // Ngày xin ứng trước

    @Column(name = "request_reason", columnDefinition = "TEXT")
    private String requestReason; // Lý do tạm ứng tiền

    @Column(name = "advanced_amount")
    private Double advancedAmount; // Số tiền ứng  trước

    @Column(name = "approval_status")
    private Integer approvalStatus; // Trạng thái xác nhận. Chi tiết trong: HrConstants.StaffAdvancePaymentApprovalStatus


    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public SalaryPeriod getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriod salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
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

    public Double getAdvancedAmount() {
        return advancedAmount;
    }

    public void setAdvancedAmount(Double advancedAmount) {
        this.advancedAmount = advancedAmount;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
