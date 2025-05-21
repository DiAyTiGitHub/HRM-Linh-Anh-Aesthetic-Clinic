package com.globits.hr.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Profession;

@Table(name = "tbl_staff_family_relationship")
@Entity
public class StaffFamilyRelationship extends BaseObject {
	private static final long serialVersionUID = -8622188016671862810L;

	@ManyToOne
	@JoinColumn(name = "staff_id")
	private Staff staff;

	@ManyToOne
	@JoinColumn(name = "family_relationship_id")
	private FamilyRelationship familyRelationship;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "birth_date")
	private Date birthDate;

	@ManyToOne
	@JoinColumn(name = "profession_id")
	private Profession profession;

	@Column(name = "address")
	private String address;

	@Column(name = "description")
	private String description;

	@Column(name = "working_place")
	private String workingPlace; //nơi làm việc

	@Column(name = "is_dependent")
	private Boolean isDependent; // là người phụ thuộc => Dùng để tính người phụ thuộc trong bảng lương

	private String taxCode; // Mã số thuế
	
	@Column(name = "dependent_deduction_from_date")
	private Date dependentDeductionFromDate;
	
	@Column(name = "dependent_deduction_to_date")
	private Date dependentDeductionToDate;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public FamilyRelationship getFamilyRelationship() {
		return familyRelationship;
	}

	public void setFamilyRelationship(FamilyRelationship familyRelationship) {
		this.familyRelationship = familyRelationship;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Profession getProfession() {
		return profession;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWorkingPlace() {
		return workingPlace;
	}

	public void setWorkingPlace(String workingPlace) {
		this.workingPlace = workingPlace;
	}

	public Boolean getIsDependent() {
		return isDependent;
	}

	public void setIsDependent(Boolean isDependent) {
		this.isDependent = isDependent;
	}

	public Boolean getDependent() {
		return isDependent;
	}

	public void setDependent(Boolean dependent) {
		isDependent = dependent;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public Date getDependentDeductionFromDate() {
		return dependentDeductionFromDate;
	}

	public void setDependentDeductionFromDate(Date dependentDeductionFromDate) {
		this.dependentDeductionFromDate = dependentDeductionFromDate;
	}

	public Date getDependentDeductionToDate() {
		return dependentDeductionToDate;
	}

	public void setDependentDeductionToDate(Date dependentDeductionToDate) {
		this.dependentDeductionToDate = dependentDeductionToDate;
	}
	
	
}
