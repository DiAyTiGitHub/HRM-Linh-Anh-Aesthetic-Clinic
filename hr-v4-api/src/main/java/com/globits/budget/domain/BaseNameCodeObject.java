package com.globits.budget.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
@MappedSuperclass
public class BaseNameCodeObject extends BaseObject {
	@Column(name="name")
	private String name;
	@Column(name="code")
	private String code;
	
	@Column(name="description", length = 4000)
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
