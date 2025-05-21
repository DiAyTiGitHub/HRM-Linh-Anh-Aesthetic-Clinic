package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.salary.domain.SalaryItem;

import jakarta.persistence.*;

/*
 * Địa điểm làm việc
 */
@Entity
@Table(name = "tbl_workplace")
public class Workplace extends BaseObject {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
