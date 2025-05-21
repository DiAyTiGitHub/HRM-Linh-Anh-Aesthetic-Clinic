package com.globits.hr.dto;

import com.globits.hr.domain.EducationalInstitution;
import com.globits.core.dto.BaseObjectDto;

public class EducationalInstitutionDto extends BaseObjectDto {
	private String name;
	private String code;
	private String nameEng;
	private String description;

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

	public String getNameEng() {
		return nameEng;
	}

	public void setNameEng(String nameEng) {
		this.nameEng = nameEng;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EducationalInstitutionDto() {
	}

	public EducationalInstitutionDto(String code, String name, String nameEng, String description) {
		this.code = code;
		this.name = name;
		this.nameEng = nameEng;
		this.description = description;
	}

	public EducationalInstitutionDto(EducationalInstitution entity) {
		if (entity != null) {
			this.name = entity.getName();
			this.id = entity.getId();
			this.code = entity.getCode();
			this.nameEng = entity.getNameEng();
			this.description = entity.getDescription();
		}
	}

	@Override
	public String toString() {
		return "EducationalInstitutionDto{" + "name='" + name + '\'' + ", nameEng='" + nameEng + '\'' + ", code='"
				+ code + '\'' + ", description='" + description + '\'' + ", id=" + id + '}';
	}
}
