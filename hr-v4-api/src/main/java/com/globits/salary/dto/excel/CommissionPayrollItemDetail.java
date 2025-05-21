package com.globits.salary.dto.excel;

import com.globits.salary.domain.SalaryResultStaffItem;

import java.util.UUID;

public class CommissionPayrollItemDetail {
    // Thuộc phiếu lương nào
    private UUID payslipId;
    // Gía trị trong phiếu lương
    private String value;

    public CommissionPayrollItemDetail() {

    }

    public CommissionPayrollItemDetail(SalaryResultStaffItem srsi) {
        if (srsi == null || srsi.getSalaryResultStaff() == null) return;

        this.payslipId = srsi.getSalaryResultStaff().getId();
        this.value = srsi.getValue();
    }

    public UUID getPayslipId() {
        return payslipId;
    }

    public void setPayslipId(UUID payslipId) {
        this.payslipId = payslipId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
