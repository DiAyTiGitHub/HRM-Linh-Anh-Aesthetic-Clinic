package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.FamilyRelationship;

public class FamilyRelationshipDto extends BaseObjectDto {
    private String name;
    private String code;
    private String description;

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

    public FamilyRelationshipDto() {
        super();
    }

    public FamilyRelationshipDto(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public FamilyRelationshipDto(FamilyRelationship entity) {
        super();
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
        }
    }

    public FamilyRelationshipDto(FamilyRelationship entity, boolean b) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
        }
    }
}

