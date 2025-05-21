package com.globits.salary.dto.search;

import com.globits.salary.dto.SalaryResultItemDto;

import java.util.List;
import java.util.UUID;

public class ChooseSalaryResultItemDto {
    private List<SalaryResultItemDto> currentResultItems;

    private List<UUID> chosenItemIds;
    private UUID salaryResultId;

    public ChooseSalaryResultItemDto() {
    }

    public List<SalaryResultItemDto> getCurrentResultItems() {
        return currentResultItems;
    }

    public void setCurrentResultItems(List<SalaryResultItemDto> currentResultItems) {
        this.currentResultItems = currentResultItems;
    }

    public List<UUID> getChosenItemIds() {
        return chosenItemIds;
    }

    public void setChosenItemIds(List<UUID> chosenItemIds) {
        this.chosenItemIds = chosenItemIds;
    }

    public UUID getSalaryResultId() {
        return salaryResultId;
    }

    public void setSalaryResultId(UUID salaryResultId) {
        this.salaryResultId = salaryResultId;
    }
}
