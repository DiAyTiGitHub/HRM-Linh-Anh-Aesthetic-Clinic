package com.globits.salary.dto;

import com.globits.hr.dto.StaffDto;

public class RequestSalaryValueDto {
    private SalaryTemplateDto salaryTemplate;
    private StaffDto staff;
    private Boolean getAll = false;

    public RequestSalaryValueDto() {
    }

    public RequestSalaryValueDto(SalaryTemplateDto salaryTemplate, StaffDto staff) {
        this.salaryTemplate = salaryTemplate;
        this.staff = staff;
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

    public Boolean getGetAll() {
        return getAll;
    }

    public void setGetAll(Boolean getAll) {
        this.getAll = getAll;
    }
}
