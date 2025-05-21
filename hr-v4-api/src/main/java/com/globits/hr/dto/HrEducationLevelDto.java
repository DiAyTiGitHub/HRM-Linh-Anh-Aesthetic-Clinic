package com.globits.hr.dto;

import com.globits.hr.domain.HrEducationLevel;
import com.globits.core.dto.BaseObjectDto;

public class HrEducationLevelDto extends BaseObjectDto {
    private String name;
    private String code;
    private String description;
    private String numbericCode;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumbericCode() {
        return numbericCode;
    }

    public void setNumbericCode(String numbericCode) {
        this.numbericCode = numbericCode;
    }

    public HrEducationLevelDto() {
    }

    public HrEducationLevelDto(HrEducationLevel el) {
        this.code = el.getCode();
        this.name = el.getName();
        this.description = el.getDescription();
        this.numbericCode = el.getNumbericCode();
    }
}
