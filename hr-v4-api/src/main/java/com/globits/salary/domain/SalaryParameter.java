package com.globits.salary.domain;

import java.util.Date;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "tbl_salary_parameter")
@Entity
public class SalaryParameter extends BaseObject {

    @Column(name = "leave_approval_lock_date")
    private Integer leaveApprovalLockDate;// ngay khoa cong,phep

    @Column(name = "leave_approval_lock_time")
    private String leaveApprovalLockTime;// gio khoa cong,phep

    @Column(name = "monthly_payroll_lock_date")
    private Integer monthlyPayrollLockDate;// ngay khoa bang luong thang

    @Column(name = "monthly_payroll_lock_time")
    private String monthlyPayrollLockTime;// gio khoa bang luong thang

    @Column(name = "default_payroll_cycle_start")
    private Integer defaultPayrollCycleStart;// ngay bat dau chu ky luong mac dinh

    @Column(name = "mid_cycle_payroll_start")
    private String midCyclePayrollStart; // ngay bat dau ky luong giua ky

    @Column(name = "approve_attendance_duration")
    private Integer approveAttendanceDuration;// khoa duyet cong cua QLTT sau (ngay)

    @Column(name = "can_admin_update")
    private Boolean canAdminUpdate = true;

    @Column(name = "effective_date")
    private Date effectiveDate;// ngay ap dung

    public Integer getLeaveApprovalLockDate() {
        return leaveApprovalLockDate;
    }

    public void setLeaveApprovalLockDate(Integer leaveApprovalLockDate) {
        this.leaveApprovalLockDate = leaveApprovalLockDate;
    }

    public String getLeaveApprovalLockTime() {
        return leaveApprovalLockTime;
    }

    public void setLeaveApprovalLockTime(String leaveApprovalLockTime) {
        this.leaveApprovalLockTime = leaveApprovalLockTime;
    }

    public Integer getMonthlyPayrollLockDate() {
        return monthlyPayrollLockDate;
    }

    public void setMonthlyPayrollLockDate(Integer monthlyPayrollLockDate) {
        this.monthlyPayrollLockDate = monthlyPayrollLockDate;
    }

    public String getMonthlyPayrollLockTime() {
        return monthlyPayrollLockTime;
    }

    public void setMonthlyPayrollLockTime(String monthlyPayrollLockTime) {
        this.monthlyPayrollLockTime = monthlyPayrollLockTime;
    }

    public Integer getDefaultPayrollCycleStart() {
        return defaultPayrollCycleStart;
    }

    public void setDefaultPayrollCycleStart(Integer defaultPayrollCycleStart) {
        this.defaultPayrollCycleStart = defaultPayrollCycleStart;
    }

    public String getMidCyclePayrollStart() {
        return midCyclePayrollStart;
    }

    public void setMidCyclePayrollStart(String midCyclePayrollStart) {
        this.midCyclePayrollStart = midCyclePayrollStart;
    }

    public Integer getApproveAttendanceDuration() {
        return approveAttendanceDuration;
    }

    public void setApproveAttendanceDuration(Integer approveAttendanceDuration) {
        this.approveAttendanceDuration = approveAttendanceDuration;
    }

    public Boolean getCanAdminUpdate() {
        return canAdminUpdate;
    }

    public void setCanAdminUpdate(Boolean canAdminUpdate) {
        this.canAdminUpdate = canAdminUpdate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

}
