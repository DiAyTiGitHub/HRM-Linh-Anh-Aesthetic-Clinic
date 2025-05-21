package com.globits.hr.dto;

import com.globits.hr.domain.HrEducationType;
import com.globits.core.dto.BaseObjectDto;

public class HrEducationTypeDto extends BaseObjectDto {
    private String name;
    private String nameEng;//tên tiếng anh
    private String code;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
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

    public HrEducationTypeDto(HrEducationType entity) {
        super();
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.nameEng = entity.getNameEng();
            this.description = entity.getDescription();
        }
    }
    
    public HrEducationTypeDto(String code, String name, String nameEng, String description) {
    	super();
    	this.code = code;
    	this.name = name;
    	this.nameEng = nameEng;
    	this.description = description;
    }

    @Override
    public String toString() {
        return "HrEducationTypeDto{" +
                "name='" + name + '\'' +
                ", nameEng='" + nameEng + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }

    public HrEducationTypeDto() {
    }
}

