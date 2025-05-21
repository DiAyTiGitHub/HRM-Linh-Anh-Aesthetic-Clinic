package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "tbl_salary_unit")
@Entity
public class SalaryUnit extends BaseObject {

    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "man_days")
    private Double manDays;

    public SalaryUnit() {

    }

    public SalaryUnit(String name, String code, Double manDays) {
        this.name = name;
        this.code = code;
        this.manDays = manDays;
    }

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

    public Double getManDays() {
        return manDays;
    }

    public void setManDays(Double manDays) {
        this.manDays = manDays;
    }

}
