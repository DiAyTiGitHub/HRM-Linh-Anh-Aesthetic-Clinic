package com.globits.salary.dto;

import java.util.List;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.StaffSalaryItemValue;

public class StaffSalaryItemValueItemDto extends BaseObjectDto {
    private SalaryItemDto salaryItem;
    private Double value;
    private Integer calculationType;
    
    public StaffSalaryItemValueItemDto() {
    }

    public StaffSalaryItemValueItemDto(StaffSalaryItemValue entity, Boolean isDetail) {
        super(entity);
        if (entity != null) {
        }
    }

    public StaffSalaryItemValueItemDto(StaffSalaryItemValue entity) {
        this(entity, true);
    }

	public SalaryItemDto getSalaryItem() {
		return salaryItem;
	}

	public void setSalaryItem(SalaryItemDto salaryItem) {
		this.salaryItem = salaryItem;
	}
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(Integer calculationType) {
		this.calculationType = calculationType;
	}
}
