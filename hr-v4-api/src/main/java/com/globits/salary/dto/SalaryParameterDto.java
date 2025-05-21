package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryParameter;

import java.util.Date;

public class SalaryParameterDto extends BaseObjectDto {
    private Integer leaveApprovalLockDate;
    private String leaveApprovalLockTime;
    private Integer monthlyPayrollLockDate;
    private String monthlyPayrollLockTime;
    private Integer defaultPayrollCycleStart;
    private String midCyclePayrollStart;
    private Integer approveAttendanceDuration;
    private Boolean canAdminUpdate;
    private Date effectiveDate;


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

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Boolean getCanAdminUpdate() {
        return canAdminUpdate;
    }

    public void setCanAdminUpdate(Boolean canAdminUpdate) {
        this.canAdminUpdate = canAdminUpdate;
    }

    public SalaryParameterDto() {
        super();
    }

    public SalaryParameterDto(SalaryParameter salaryParameter) {
        if (salaryParameter != null) {
            this.setId(salaryParameter.getId());
            this.setLeaveApprovalLockDate(salaryParameter.getLeaveApprovalLockDate());
            this.setLeaveApprovalLockTime(salaryParameter.getLeaveApprovalLockTime());
            this.setMonthlyPayrollLockDate(salaryParameter.getMonthlyPayrollLockDate());
            this.setMonthlyPayrollLockTime(salaryParameter.getMonthlyPayrollLockTime());
            this.setDefaultPayrollCycleStart(salaryParameter.getDefaultPayrollCycleStart());
            this.setMidCyclePayrollStart(salaryParameter.getMidCyclePayrollStart());
            this.setApproveAttendanceDuration(salaryParameter.getApproveAttendanceDuration());
            this.setCanAdminUpdate(salaryParameter.getCanAdminUpdate());
            this.setEffectiveDate(salaryParameter.getEffectiveDate());
        }
    }
}
