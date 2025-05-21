package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.RefusalReason;

public class RefusalReasonDto extends BaseObjectDto {

    private String code;        // ma ly do
    private String name;        // ly do tu choi
    private String description; // mo ta

    public RefusalReasonDto() {
    }

    public RefusalReasonDto(String name, String code, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public RefusalReasonDto(RefusalReason entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
        }
    }

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
