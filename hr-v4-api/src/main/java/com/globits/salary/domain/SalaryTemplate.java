package com.globits.salary.domain;

import java.util.Set;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

// Mẫu bảng lương
@Table(name = "tbl_salary_template")
@Entity
public class SalaryTemplate extends BaseObject {
	private static final long serialVersionUID = 1L;

	private String code;
	private String name;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "is_active")
	private Boolean isActive; // Đang còn được sử dụng hay không. VD: = false => Không thể chọn sử dụng mẫu
								// bảng lương này cho bảng lương mới nữa

	@Column(name = "is_create_payslip")
	private Boolean isCreatePayslip; // Có tạo phiếu lương cho bảng lương sử dụng mẫu bảng lương này hay không

	@OneToMany(mappedBy = "salaryTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SalaryTemplateItemGroup> templateItemGroups; // các nhóm cột trong mẫu bảng lương

	@OneToMany(mappedBy = "salaryTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("displayOrder")
	private Set<SalaryTemplateItem> templateItems; // thành phần lương chính là các cột trong mẫu bảng lương

	@OneToMany(mappedBy = "salaryTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<StaffSalaryTemplate> staffSalaryTemplates; // các nhân viên sử dụng mẫu bảng lương


	public Set<StaffSalaryTemplate> getStaffSalaryTemplates() {
		return staffSalaryTemplates;
	}

	public void setStaffSalaryTemplates(Set<StaffSalaryTemplate> staffSalaryTemplates) {
		this.staffSalaryTemplates = staffSalaryTemplates;
	}

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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Set<SalaryTemplateItemGroup> getTemplateItemGroups() {
		return templateItemGroups;
	}

	public void setTemplateItemGroups(Set<SalaryTemplateItemGroup> templateItemGroups) {
		this.templateItemGroups = templateItemGroups;
	}

	public Set<SalaryTemplateItem> getTemplateItems() {
		return templateItems;
	}

	public void setTemplateItems(Set<SalaryTemplateItem> templateItems) {
		this.templateItems = templateItems;
	}

	public Boolean getIsCreatePayslip() {
		return isCreatePayslip;
	}

	public void setIsCreatePayslip(Boolean isCreatePayslip) {
		this.isCreatePayslip = isCreatePayslip;
	}
	
	

}
