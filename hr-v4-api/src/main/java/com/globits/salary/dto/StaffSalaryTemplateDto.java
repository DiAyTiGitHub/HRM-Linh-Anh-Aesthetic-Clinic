package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.domain.StaffSalaryTemplate;

import java.util.List;

public class StaffSalaryTemplateDto extends BaseObjectDto {
    private StaffDto staff; // nhân viên dùng mẫu bảng lương
    private List<StaffDto> staffs; // nhân viên dùng mẫu bảng lương
    private SalaryTemplateDto salaryTemplate; // Mẫu bảng lương được dùng

    private List<StaffSalaryItemValueDto> staffSalaryItemValue; // chỉ số mẫu bảng lương ứng với nhân viên

    public StaffSalaryTemplateDto() {

    }

    public StaffSalaryTemplateDto(StaffSalaryTemplate entity) {
        this.id = entity.getId();
        if (entity.getStaff() != null) {
            StaffDto staffDto = new StaffDto(entity.getStaff(), false, false);
            this.staff = staffDto;
        }
        if (entity.getSalaryTemplate() != null) {
            SalaryTemplateDto salaryTemplateDto = new SalaryTemplateDto(entity.getSalaryTemplate());
            this.salaryTemplate = salaryTemplateDto;
        }
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public SalaryTemplateDto getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplateDto salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public List<StaffSalaryItemValueDto> getStaffSalaryItemValue() {
        return staffSalaryItemValue;
    }

    public void setStaffSalaryItemValue(List<StaffSalaryItemValueDto> staffSalaryItemValue) {
        this.staffSalaryItemValue = staffSalaryItemValue;
    }

    public List<StaffDto> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<StaffDto> staffs) {
        this.staffs = staffs;
    }
}
