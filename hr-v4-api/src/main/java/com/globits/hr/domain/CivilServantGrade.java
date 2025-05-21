package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;
/*
 * Bậc công chức (cùng Category thì có thể có nhiều grade)
 */

@Table(name = "tbl_civil_servant_grade")
@Entity
public class CivilServantGrade extends BaseObject {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "grade")
	private Integer grade;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "next_grade_id")
	private Integer nextGradeId;
	
	@Column(name = "max_grade")
	private Integer maxGrade;
	
	@Column(name = "salary_coefficient")
	private Double salaryCoefficient; // he so luong
	
	@Column(name = "description")
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
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

	public Integer getNextGradeId() {
		return nextGradeId;
	}

	public void setNextGradeId(Integer nextGradeId) {
		this.nextGradeId = nextGradeId;
	}

	public Integer getMaxGrade() {
		return maxGrade;
	}

	public void setMaxGrade(Integer maxGrade) {
		this.maxGrade = maxGrade;
	}

	public Double getSalaryCoefficient() {
		return salaryCoefficient;
	}

	public void setSalaryCoefficient(Double salaryCoefficient) {
		this.salaryCoefficient = salaryCoefficient;
	}
}
