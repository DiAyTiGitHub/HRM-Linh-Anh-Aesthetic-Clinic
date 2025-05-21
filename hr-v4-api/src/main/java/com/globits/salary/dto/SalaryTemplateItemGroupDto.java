package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItemGroup;

import java.util.UUID;

import org.springframework.util.CollectionUtils;

public class SalaryTemplateItemGroupDto extends BaseObjectDto {
    private String name;
    private String description;
    private UUID salaryTemplateId;
    public SalaryTemplateItemGroupDto() {

    }

    public SalaryTemplateItemGroupDto(SalaryTemplateItemGroup entity) {
        super();

        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        if (entity.getSalaryTemplate() != null)
            this.salaryTemplateId = entity.getSalaryTemplate().getId();
    }

    public SalaryTemplateItemGroupDto(SalaryTemplateItemGroup entity, Boolean isDetail) {
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

    public UUID getSalaryTemplateId() {
        return salaryTemplateId;
    }

    public void setSalaryTemplateId(UUID salaryTemplateId) {
        this.salaryTemplateId = salaryTemplateId;
    }
}
