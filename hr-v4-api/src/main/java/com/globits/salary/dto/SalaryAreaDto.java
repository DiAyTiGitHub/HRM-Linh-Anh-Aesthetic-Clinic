package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryArea;

public class SalaryAreaDto extends BaseObjectDto {

    private String name;
    private String code;
    private Double minHour;
    private Double minMonth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getMinHour() {
        return minHour;
    }

    public void setMinHour(Double minHour) {
        this.minHour = minHour;
    }

    public Double getMinMonth() {
        return minMonth;
    }

    public void setMinMonth(Double minMonth) {
        this.minMonth = minMonth;
    }

    public SalaryAreaDto() {
    }

    public SalaryAreaDto(SalaryArea salaryArea) {
        if (salaryArea != null) {
            this.id = salaryArea.getId();
            this.setName(salaryArea.getName());
            this.setCode(salaryArea.getCode());
            this.setMinHour(salaryArea.getMinHour());
            this.setMinMonth(salaryArea.getMinMonth());
        }
    }

}
