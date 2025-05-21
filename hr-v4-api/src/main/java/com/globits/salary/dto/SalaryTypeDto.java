package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryType;

public class SalaryTypeDto extends BaseObjectDto {
	private String name;
	private String otherName;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SalaryTypeDto() {
		super();
	}

	public SalaryTypeDto(SalaryType salaryType) {
		if (salaryType != null) {
			this.setId(salaryType.getId());
			this.setName(salaryType.getName());
			this.setOtherName(salaryType.getOtherName());
			this.setDescription(salaryType.getDescription());
		}

	}
}
