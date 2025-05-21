package com.globits.hr.dto;

import com.globits.hr.domain.Language;
import com.globits.core.dto.BaseObjectDto;

public class LanguageDto extends BaseObjectDto {
    private String name;
    private String code;

    public LanguageDto() {

    }

    public LanguageDto(Language input) {
        if (input != null) {
            this.setId(input.getId());
            this.setName(input.getName());
            this.setCode(input.getCode());
        }

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

}
