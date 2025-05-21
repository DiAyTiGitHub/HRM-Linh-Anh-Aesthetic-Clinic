package com.globits.salary.dto.excel;

public class SalaryResultStaffItemImportDto {
    private String staffCode;
    private String salaryPeriodCode; // Mã kỳ lương
    private String salaryTemplateCode; // Mã mẫu bảng lương
    private String salaryItemCode;
    private String salaryItemValue;
    // Có tính lại phiếu lương của nhân viên (salaryResultStaff)
    // sau khi import dữ liệu vào salaryResultStaffItem hay không
    private Boolean recalculateStaffPayslipAfterProcess;

    public SalaryResultStaffItemImportDto(){

    }

    public String getSalaryTemplateCode() {
        return salaryTemplateCode;
    }

    public void setSalaryTemplateCode(String salaryTemplateCode) {
        this.salaryTemplateCode = salaryTemplateCode;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getSalaryPeriodCode() {
        return salaryPeriodCode;
    }

    public void setSalaryPeriodCode(String salaryPeriodCode) {
        this.salaryPeriodCode = salaryPeriodCode;
    }

    public String getSalaryItemCode() {
        return salaryItemCode;
    }

    public void setSalaryItemCode(String salaryItemCode) {
        this.salaryItemCode = salaryItemCode;
    }

    public String getSalaryItemValue() {
        return salaryItemValue;
    }

    public void setSalaryItemValue(String salaryItemValue) {
        this.salaryItemValue = salaryItemValue;
    }

    public Boolean getRecalculateStaffPayslipAfterProcess() {
        return recalculateStaffPayslipAfterProcess;
    }

    public void setRecalculateStaffPayslipAfterProcess(Boolean recalculateStaffPayslipAfterProcess) {
        this.recalculateStaffPayslipAfterProcess = recalculateStaffPayslipAfterProcess;
    }
}
