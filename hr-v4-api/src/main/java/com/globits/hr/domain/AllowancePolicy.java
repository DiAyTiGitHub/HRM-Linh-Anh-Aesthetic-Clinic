package com.globits.hr.domain;

import java.util.Date;
import java.util.Set;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Department;
import com.globits.core.domain.Organization;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
/*
 * Chính sách phụ cấp
 */

@Entity
@Table(name = "tbl_allowance_policy")
public class AllowancePolicy extends BaseObject {
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "position_id")
	private Position position;
	
	// Phụ cấp
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "allowance_id")
	private Allowance allowance;
	
	// Công thức/Giá trị để tính khoản phụ cấp trong chính sách
	@Column(name = "formula")
	private String formula;
	
	// Ngày bắt đầu hiệu lực
	@Column(name = "start_date")
	private Date startDate;
	
	// Ngày kết thúc hiệu lực
	@Column(name = "end_date")
	private Date endDate;
	
	@OneToMany(mappedBy = "allowancePolicy", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<StaffAllowance> staffAllowances;
	
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
	
	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	public Department getDepartment() {
		return department;
	}
	
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public Allowance getAllowance() {
		return allowance;
	}
	
	public void setAllowance(Allowance allowance) {
		this.allowance = allowance;
	}
	
	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Set<StaffAllowance> getStaffAllowances() {
		return staffAllowances;
	}

	public void setStaffAllowances(Set<StaffAllowance> staffAllowances) {
		this.staffAllowances = staffAllowances;
	}
	    
}
