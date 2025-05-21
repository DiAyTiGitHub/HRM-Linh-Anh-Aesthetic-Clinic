package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;


@Table(name = "tbl_employee_status")
@Entity
public class EmployeeStatus extends BaseObject {
    private static final long serialVersionUID = 1L;
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "language_key")
    private String languageKey;

    @Column(name = "is_active")
    private Boolean isActive = true;

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

    public String getLanguageKey() {
        return languageKey;
    }

    public void setLanguageKey(String languageKey) {
        this.languageKey = languageKey;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
