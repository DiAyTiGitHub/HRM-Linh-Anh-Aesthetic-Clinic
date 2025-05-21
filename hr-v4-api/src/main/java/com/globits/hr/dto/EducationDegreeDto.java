package com.globits.hr.dto;

/*
 * author Giang-Tuan Anh
 */
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.EducationDegree;

public class EducationDegreeDto extends BaseObjectDto {
	private String name;
	private String code;
	private Integer level;

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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public EducationDegreeDto() {
	}// thiếu sẽ báo lỗi 400 khi post dữ liệu

	public EducationDegreeDto(EducationDegree educationDegree) {
		if (educationDegree != null) {
			this.setId(educationDegree.getId());
			this.setCode(educationDegree.getCode());
			this.setName(educationDegree.getName());
			this.level = educationDegree.getLevel();
		}
	}

	public EducationDegreeDto(String code, String name, Integer level) {
		this.code = code;
		this.name = name;
		this.level = level;
	}
}
