package com.globits.hr.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;


@Table(name = "tbl_overtime_type")
@Entity
public class OvertimeType extends BaseObject {
    private static final long serialVersionUID = 1L;
    private String code;
    private String name;

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

}
