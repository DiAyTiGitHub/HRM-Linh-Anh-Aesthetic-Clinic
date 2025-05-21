package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.ContractType;

public class ContractTypeDto extends BaseObjectDto {
	
	private String name;
	
	private String code;
	
	private String languageKey;

	private String description; //mo ta

	private Integer duration; // thoi han (thang)

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLanguageKey() {
		return languageKey;
	}

	public void setLanguageKey(String languageKey) {
		this.languageKey = languageKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public ContractTypeDto() {}

	public ContractTypeDto(String name, String code, String languageKey, String description, Integer duration) {
		this.name = name;
		this.code = code;
		this.languageKey = languageKey;
		this.description = description;
		this.duration = duration;
	}

	public ContractTypeDto(ContractType entity) {
		if (entity != null) {
			this.setId(entity.getId());
			this.setCode(entity.getCode());
			this.setName(entity.getName());
			this.setLanguageKey(entity.getLanguageKey());
			this.setDescription(entity.getDescription());
			this.setDuration(entity.getDuration());
		}
	}
}
