package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryResultItemGroup;
import com.globits.salary.domain.SalaryTemplateItemGroup;

import java.util.UUID;

// Nhóm thành phần lương hiển thị trên bảng lương
// Nhóm thành phần => Nhóm các cột trên bảng hiển thị
public class SalaryResultItemGroupDto extends BaseObjectDto {
    private String name;
    private String description;
    private UUID salaryResultId;

    public SalaryResultItemGroupDto() {

    }

    public SalaryResultItemGroupDto(SalaryResultItemGroup entity) {
        super();

        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();

        if (entity.getSalaryResult() != null) {
            this.salaryResultId = entity.getSalaryResult().getId();
        }
    }

    public SalaryResultItemGroupDto(SalaryResultItemGroup entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

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

    public UUID getSalaryResultId() {
        return salaryResultId;
    }

    public void setSalaryResultId(UUID salaryResultId) {
        this.salaryResultId = salaryResultId;
    }
}
