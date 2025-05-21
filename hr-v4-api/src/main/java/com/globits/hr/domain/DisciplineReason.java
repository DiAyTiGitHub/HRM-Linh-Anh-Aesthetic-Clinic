package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// ly do ky luat
@Table(name = "tbl_discipline_reason")
@Entity
public class DisciplineReason extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // ma ly do
    private String name; // ly do ky luat

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // mo ta

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

