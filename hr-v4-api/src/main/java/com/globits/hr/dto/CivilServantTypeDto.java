package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.CivilServantType;

public class CivilServantTypeDto extends BaseObjectDto {
    private String code;
    private String name;
    private String languageKey;

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

    public CivilServantTypeDto() {
        super();
    }

    public CivilServantTypeDto(CivilServantType entity) {
        super();
        if (entity != null) {
            this.setId(entity.getId());
            this.setCode(entity.getCode());
            this.setName(entity.getName());
            this.setLanguageKey(entity.getLanguageKey());
        }
    }

    public CivilServantTypeDto(String code, String name, String languageKey) {
        this.code = code;
        this.name = name;
        this.languageKey = languageKey;
    }

}
