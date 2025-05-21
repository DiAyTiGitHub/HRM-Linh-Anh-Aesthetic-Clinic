package com.globits.salary.dto;

import java.util.List;

public class MapStaffSalaryItemValueDto {
    private SalaryItemDto salaryItem;

    private List<StaffSalaryItemValueDto> staffSalaryItemValues;

    public MapStaffSalaryItemValueDto() {
    }

    public List<StaffSalaryItemValueDto> getStaffSalaryItemValues() {
        return staffSalaryItemValues;
    }

    public void setStaffSalaryItemValues(List<StaffSalaryItemValueDto> staffSalaryItemValues) {
        this.staffSalaryItemValues = staffSalaryItemValues;
    }

    public SalaryItemDto getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItemDto salaryItem) {
        this.salaryItem = salaryItem;
    }
}
