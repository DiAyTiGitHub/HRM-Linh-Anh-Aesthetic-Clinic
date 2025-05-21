package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.salary.domain.SalaryItem;

import jakarta.persistence.*;
/*
 * Phụ cấp
 */
@Entity
@Table(name = "tbl_allowance")
public class Allowance extends BaseObject {
	
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allowance_type_id")
    private AllowanceType allowanceType;
    
	@OneToOne(mappedBy = "allowance", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	private SalaryItem salaryItem;
    
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

	public AllowanceType getAllowanceType() {
		return allowanceType;
	}

	public void setAllowanceType(AllowanceType allowanceType) {
		this.allowanceType = allowanceType;
	}

	public SalaryItem getSalaryItem() {
		return salaryItem;
	}

	public void setSalaryItem(SalaryItem salaryItem) {
		this.salaryItem = salaryItem;
	}

}
