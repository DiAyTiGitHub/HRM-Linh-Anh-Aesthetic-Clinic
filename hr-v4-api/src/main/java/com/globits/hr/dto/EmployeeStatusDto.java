package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.EmployeeStatus;

public class EmployeeStatusDto extends BaseObjectDto {
    private String code;
    private String name;
    private String languageKey;

    private Boolean isActive;

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

    public EmployeeStatusDto() {

    }

    public EmployeeStatusDto(String code, String name, String languageKey, Boolean isActive) {
        this.code = code;
        this.name = name;
        this.languageKey = languageKey;
        this.isActive = isActive;
    }

    public EmployeeStatusDto(EmployeeStatus entity) {
        if (entity != null) {
            this.setCode(entity.getCode());
            this.setName(entity.getName());
            this.setId(entity.getId());
            this.setLanguageKey(entity.getLanguageKey());
            this.setActive(entity.isActive());
        }
    }

}
