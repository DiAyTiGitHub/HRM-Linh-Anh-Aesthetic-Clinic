package com.globits.hr.dto;

import com.globits.hr.domain.StateManagementLevel;
import com.globits.core.dto.BaseObjectDto;

public class StateManagementLevelDto extends BaseObjectDto {
    private String name;
    private String code;
    private String level;

    public StateManagementLevelDto() {

    }

    public StateManagementLevelDto(StateManagementLevel entity) {

        if (entity != null) {
            this.setId(entity.getId());
            this.setCode(entity.getCode());
            this.setName(entity.getName());
            this.setLevel(entity.getLevel());
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

}
