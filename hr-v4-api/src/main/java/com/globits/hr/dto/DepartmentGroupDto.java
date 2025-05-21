package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.DepartmentGroup;

public class DepartmentGroupDto extends BaseObjectDto {


    private String name; // Tên đơn vị

    private String otherName; // Tên khác

    private String shortName; // Tên viết tắt

    private Integer sortNumber; // Thứ tự

    private String description; // Mô tả

    public DepartmentGroupDto() {
    }

    public DepartmentGroupDto(String name, String otherName, String shortName, Integer sortNumber, String description) {
        this.name = name;
        this.otherName = otherName;
        this.shortName = shortName;
        this.sortNumber = sortNumber;
        this.description = description;
    }

    public DepartmentGroupDto(DepartmentGroup dto) {
        if (dto != null) {
            this.id = dto.getId();
            this.description = dto.getDescription();
            this.name = dto.getName();
            this.otherName = dto.getOtherName();
            this.shortName = dto.getShortName();
            this.sortNumber = dto.getSortNumber();
        }
    }

    public String getName() {
        return name;
    }

    public String getOtherName() {
        return otherName;
    }

    public String getShortName() {
        return shortName;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public String getDescription() {
        return description;
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

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
