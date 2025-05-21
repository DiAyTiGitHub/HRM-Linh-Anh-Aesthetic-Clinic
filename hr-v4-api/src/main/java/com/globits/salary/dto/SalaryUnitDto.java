package com.globits.salary.dto;

import com.globits.core.domain.BaseObject;
import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryUnit;

public class SalaryUnitDto extends BaseObjectDto {
    private String name;
    private String code;
    private Double manDays;

    public SalaryUnitDto() {
        super();
    }
    public SalaryUnitDto(SalaryUnit salaryUnit) {
        if (salaryUnit != null) {
            this.setName(salaryUnit.getName());
            this.setCode(salaryUnit.getCode());
            this.setManDays(salaryUnit.getManDays());
            this.setId(salaryUnit.getId());
        }
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Double getManDays() {
        return manDays;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setManDays(Double manDays) {
        this.manDays = manDays;
    }
}
