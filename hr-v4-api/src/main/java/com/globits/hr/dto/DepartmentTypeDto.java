package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.DepartmentType;

public class DepartmentTypeDto extends BaseObjectDto {
    private Integer sortNumber; // trong so
    private String name;        // ten phong ban
    private String code;        // ma phong ban
    private String otherName;   // ten khac
    private String shortName;   // ten viet tat
    private String description; // mo ta

    public DepartmentTypeDto() {
    }

    public DepartmentTypeDto(Integer sortNumber, String name, String code, String otherName, String shortName, String description) {
        this.sortNumber = sortNumber;
        this.name = name;
        this.code = code;
        this.otherName = otherName;
        this.shortName = shortName;
        this.description = description;
    }

    public DepartmentTypeDto(DepartmentType departmentType) {
        if (departmentType != null) {
            this.id = departmentType.getId();
            this.sortNumber = departmentType.getSortNumber();
            this.name = departmentType.getName();
            this.code = departmentType.getCode();
            this.otherName = departmentType.getOtherName();
            this.shortName = departmentType.getShortName();
            this.description = departmentType.getDescription();
        }
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOtherName() {
        return otherName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
