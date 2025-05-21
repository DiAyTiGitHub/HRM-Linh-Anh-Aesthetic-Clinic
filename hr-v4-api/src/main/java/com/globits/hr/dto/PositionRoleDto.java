package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.PositionRole;

public class PositionRoleDto extends BaseObjectDto {

    private static final long serialVersionUID = 1L;
    private String name;
    private String otherName;
    private String shortName;
    private String description;

    public PositionRoleDto() {
    }

    public PositionRoleDto(String name, String otherName, String shortName, String description) {
        this.name = name;
        this.otherName = otherName;
        this.shortName = shortName;
        this.description = description;
    }

    public PositionRoleDto(PositionRole entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.otherName = entity.getOtherName();
            this.shortName = entity.getShortName();
            this.description = entity.getDescription();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
