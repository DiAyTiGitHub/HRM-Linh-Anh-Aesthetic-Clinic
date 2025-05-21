package com.globits.salary.dto.search;

import com.globits.salary.dto.SalaryResultItemDto;
import com.globits.salary.dto.SalaryTemplateItemDto;

import java.util.List;
import java.util.UUID;

public class ChooseSalaryTemplateItemDto {
    private List<SalaryTemplateItemDto> currentTemplateItems;

    private List<UUID> chosenItemIds;
    private UUID salaryTemplateId;

    public ChooseSalaryTemplateItemDto() {
    }

    public List<SalaryTemplateItemDto> getCurrentTemplateItems() {
        return currentTemplateItems;
    }

    public void setCurrentTemplateItems(List<SalaryTemplateItemDto> currentTemplateItems) {
        this.currentTemplateItems = currentTemplateItems;
    }

    public List<UUID> getChosenItemIds() {
        return chosenItemIds;
    }

    public void setChosenItemIds(List<UUID> chosenItemIds) {
        this.chosenItemIds = chosenItemIds;
    }

    public UUID getSalaryTemplateId() {
        return salaryTemplateId;
    }

    public void setSalaryTemplateId(UUID salaryTemplateId) {
        this.salaryTemplateId = salaryTemplateId;
    }
}
