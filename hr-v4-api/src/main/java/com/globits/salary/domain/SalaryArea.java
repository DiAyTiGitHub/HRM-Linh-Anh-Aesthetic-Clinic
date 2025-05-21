package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "tbl_salary_area")
@Entity
public class SalaryArea extends BaseObject {
    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "min_hour")
    private Double minHour; // muc luong toi thieu gio

    @Column(name = "min_month")
    private Double minMonth; // muc luong toi thieu thang

    public SalaryArea() {
    }

    public SalaryArea(String name, String code, Double minHour, Double minMonth) {
        this.name = name;
        this.code = code;
        this.minHour = minHour;
        this.minMonth = minMonth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMinHour() {
        return minHour;
    }

    public void setMinHour(Double minHour) {
        this.minHour = minHour;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getMinMonth() {
        return minMonth;
    }

    public void setMinMonth(Double minMonth) {
        this.minMonth = minMonth;
    }
}
