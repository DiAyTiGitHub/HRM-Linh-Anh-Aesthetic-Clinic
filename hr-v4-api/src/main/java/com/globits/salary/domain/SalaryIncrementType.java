package com.globits.salary.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;


@Table(name = "tbl_salary_increment_type")
@Entity
public class SalaryIncrementType extends BaseObject {
    private static final long serialVersionUID = 1L;

    private String name;
    private String code;

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
}
