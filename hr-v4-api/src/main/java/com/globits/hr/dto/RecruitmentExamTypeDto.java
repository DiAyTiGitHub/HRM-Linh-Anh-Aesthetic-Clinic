package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.RecruitmentExamType;

public class RecruitmentExamTypeDto extends BaseObjectDto {
    private String name;        // ma loai kiem tra
    private String code;        // loai kiem tra
    private String description; // mo ta

    public RecruitmentExamTypeDto() {

    }

    public RecruitmentExamTypeDto(RecruitmentExamType entity) {
        if (entity == null) return;

        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.description = entity.getDescription();
    }

    public RecruitmentExamTypeDto(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

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

}

