
package com.globits.hr.dto;

import com.globits.hr.domain.ProfessionalDegree;
import com.globits.core.dto.BaseObjectDto;

public class ProfessionalDegreeDto extends BaseObjectDto {

	private String name;
	private String code;

	public ProfessionalDegreeDto() {

	}

	public ProfessionalDegreeDto(ProfessionalDegree entity) {
		if (entity != null) {
			this.setId(entity.getId());
			this.setCode(entity.getCode());
			this.setName(entity.getName());
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

}
