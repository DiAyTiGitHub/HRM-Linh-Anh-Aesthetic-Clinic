package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "tbl_salary_type")
@Entity
public class SalaryType extends BaseObject {
    @Column(name = "name")
    private String name;// ten

    @Column(name = "other_name")
    private String otherName;// ten khac

    @Column(name = "description")
    private String description;// mo ta

    public SalaryType() {

    }

    public SalaryType(String name, String otherName, String description) {
        this.name = name;
        this.otherName = otherName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
