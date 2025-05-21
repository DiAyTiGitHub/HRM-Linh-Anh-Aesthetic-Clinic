package com.globits.hr.domain;

import jakarta.persistence.*;

import com.globits.core.domain.BaseObject;
/*
 * Cấp quản lý cơ sở đào tạo
 */

@Table(name = "tbl_educational_management_Level")
@Entity	
public class EducationalManagementLevel extends BaseObject {
	private static final long serialVersionUID = 1L;
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;
	@Column(name = "level")
	private String level;

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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
}
