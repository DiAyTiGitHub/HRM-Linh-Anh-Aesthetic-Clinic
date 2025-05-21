package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.Allowance;
import com.globits.salary.dto.SalaryItemDto;

public class AllowanceDto extends BaseObjectDto {
	private String code;
	private String name;
	private String description;
	private AllowanceTypeDto allowanceType;

	private SalaryItemDto salaryItem;
	
	public AllowanceDto() {
		
	}
	
	public AllowanceDto(Allowance entity) {
		if(entity != null) {
			this.id = entity.getId();
			this.code = entity.getCode();
			this.name = entity.getName();
			this.description = entity.getDescription();
			if (entity.getAllowanceType() != null) {
                this.allowanceType = new AllowanceTypeDto(entity.getAllowanceType());
            }
			if (entity.getSalaryItem() != null) {
				this.salaryItem = new SalaryItemDto(entity.getSalaryItem());
			}
		}
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public AllowanceTypeDto getAllowanceType() {
		return allowanceType;
	}
	public void setAllowanceType(AllowanceTypeDto allowanceType) {
		this.allowanceType = allowanceType;
	}

	public SalaryItemDto getSalaryItem() {
		return salaryItem;
	}

	public void setSalaryItem(SalaryItemDto salaryItem) {
		this.salaryItem = salaryItem;
	}
	
}
