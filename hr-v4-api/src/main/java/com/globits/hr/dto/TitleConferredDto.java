package com.globits.hr.dto;

import com.globits.hr.domain.TitleConferred;
import com.globits.core.dto.BaseObjectDto;

public class TitleConferredDto extends BaseObjectDto {
	private String name;
	private String code;
	private String description;
	private Integer level;

	public TitleConferredDto() {

	}

	public TitleConferredDto(TitleConferred ac) {

		if (ac != null) {
			this.setId(ac.getId());
			this.setCode(ac.getCode());
			this.setName(ac.getName());
			this.setLevel(ac.getLevel());
		}
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

}
