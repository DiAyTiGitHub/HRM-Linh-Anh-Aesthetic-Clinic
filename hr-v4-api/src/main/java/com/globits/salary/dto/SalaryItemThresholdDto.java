package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryItemThreshold;
import jakarta.persistence.Column;

import java.util.UUID;

public class SalaryItemThresholdDto extends BaseObjectDto {
    private UUID salaryItemId;
    private Integer displayOrder;
    private String thresholdValue; // Công thức/Gía trị ngưỡng
    private String inUseValue; // Gía trị sử dụng khi đạt mức ngưỡng cao nhất

    public SalaryItemThresholdDto() {

    }

    public SalaryItemThresholdDto(SalaryItemThreshold entity) {
        super();

        this.id = entity.getId();

        if (entity.getSalaryItem() != null) {
            this.salaryItemId = entity.getSalaryItem().getId();
        }
        this.displayOrder = entity.getDisplayOrder();
        this.thresholdValue = entity.getThresholdValue();
        this.inUseValue = entity.getInUseValue();

    }

    public SalaryItemThresholdDto(SalaryItemThreshold entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

    }

    public UUID getSalaryItemId() {
        return salaryItemId;
    }

    public void setSalaryItemId(UUID salaryItemId) {
        this.salaryItemId = salaryItemId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(String thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getInUseValue() {
        return inUseValue;
    }

    public void setInUseValue(String inUseValue) {
        this.inUseValue = inUseValue;
    }
}
