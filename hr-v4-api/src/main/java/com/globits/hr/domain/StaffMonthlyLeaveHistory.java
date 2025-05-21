package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

// Thống kê ngày nghỉ phép hàng tháng của nhân viên
@Entity
@Table(name = "tbl_staff_monthly_leave_history")
public class StaffMonthlyLeaveHistory extends BaseObject {
    private static final long serialVersionUID = 6014783475303579207L;

    @ManyToOne
    @JoinColumn(name = "annual_leave_history_id")
    private StaffAnnualLeaveHistory annualLeaveHistory; // Thuộc bảng thống kê nghỉ phép năm của nhân viên nào đó

    @Column(name = "month", nullable = false)
    private Integer month; // Tháng thống kê nghỉ phép

    @Column(name = "leave_days")
    private Double leaveDays; // Số ngày nhân viên đã nghỉ trong tháng


    public StaffAnnualLeaveHistory getAnnualLeaveHistory() {
        return annualLeaveHistory;
    }

    public void setAnnualLeaveHistory(StaffAnnualLeaveHistory annualLeaveHistory) {
        this.annualLeaveHistory = annualLeaveHistory;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Double leaveDays) {
        this.leaveDays = leaveDays;
    }
}
