package com.globits.salary.dto.excel;

import com.globits.core.dto.BaseObjectDto;

import java.util.List;

// Thành phần lương trong bảng lương
public class ImportSalaryResultStaffDto extends BaseObjectDto {
    private String importOrder;
    private String staffCode; // Mã nhân viên
    private String staffDisplayName; // Họ tên nhân viên
    private String salaryPeriodCode; // Mã kỳ lương

    private List<ImportSalaryStaffItemValueDto> salaryItemValues;

    public ImportSalaryResultStaffDto() {

    }

    public String getImportOrder() {
        return importOrder;
    }

    public void setImportOrder(String importOrder) {
        this.importOrder = importOrder;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffDisplayName() {
        return staffDisplayName;
    }

    public void setStaffDisplayName(String staffDisplayName) {
        this.staffDisplayName = staffDisplayName;
    }

    public String getSalaryPeriodCode() {
        return salaryPeriodCode;
    }

    public void setSalaryPeriodCode(String salaryPeriodCode) {
        this.salaryPeriodCode = salaryPeriodCode;
    }

    public List<ImportSalaryStaffItemValueDto> getSalaryItemValues() {
        return salaryItemValues;
    }

    public void setSalaryItemValues(List<ImportSalaryStaffItemValueDto> salaryItemValues) {
        this.salaryItemValues = salaryItemValues;
    }
}
