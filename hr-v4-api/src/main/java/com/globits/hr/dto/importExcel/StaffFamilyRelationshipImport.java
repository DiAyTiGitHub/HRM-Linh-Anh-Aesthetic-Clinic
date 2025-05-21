package com.globits.hr.dto.importExcel;

import com.globits.hr.domain.StaffFamilyRelationship;

import java.util.Date;

public class StaffFamilyRelationshipImport {
    private Integer stt;
    private String staffCode;
    private String staffDisplayName;
    private String relationPersonName;
    private Date relationPersonBirthDate;
    private String professionCode;
    private String professionName;
    private String relationshipCode;
    private String relationshipName;
    private Boolean isDependent;
    private String address;
    private String workingPlace;
    private String taxCode;
    private Date dependentDeductionFromDate;
    private Date dependentDeductionToDate;
    private String errorMessage;

    public StaffFamilyRelationshipImport() {
    }

    public StaffFamilyRelationshipImport(StaffFamilyRelationship entity) {
        if (entity == null)
            return;

        if (entity.getStaff() != null) {
            this.staffCode = entity.getStaff().getStaffCode();
            this.staffDisplayName = entity.getStaff().getDisplayName();
        }

        this.relationPersonName = entity.getFullName();
        this.relationPersonBirthDate = entity.getBirthDate();

        if (entity.getProfession() != null) {
            this.professionCode = entity.getProfession().getCode();
            this.professionName = entity.getProfession().getName();
        }

        if (entity.getFamilyRelationship() != null) {
            this.relationshipCode = entity.getFamilyRelationship().getCode();
            this.relationshipName = entity.getFamilyRelationship().getName();
        }
        this.isDependent = entity.getDependent();
        if (this.isDependent != null && this.isDependent) {
            this.dependentDeductionFromDate = entity.getDependentDeductionFromDate();
            this.dependentDeductionToDate = entity.getDependentDeductionToDate();
        }
        this.address = entity.getAddress();
        this.workingPlace = entity.getWorkingPlace();
        this.taxCode = entity.getTaxCode();
    }


    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffDisplayName() {
        return staffDisplayName;
    }

    public void setStaffDisplayName(String staffDisplayName) {
        this.staffDisplayName = staffDisplayName;
    }

    public String getRelationPersonName() {
        return relationPersonName;
    }

    public void setRelationPersonName(String relationPersonName) {
        this.relationPersonName = relationPersonName;
    }

    public Date getRelationPersonBirthDate() {
        return relationPersonBirthDate;
    }

    public void setRelationPersonBirthDate(Date relationPersonBirthDate) {
        this.relationPersonBirthDate = relationPersonBirthDate;
    }

    public Integer getStt() {
        return stt;
    }

    public void setStt(Integer stt) {
        this.stt = stt;
    }

    public String getProfessionCode() {
        return professionCode;
    }

    public void setProfessionCode(String professionCode) {
        this.professionCode = professionCode;
    }

    public String getProfessionName() {
        return professionName;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }

    public String getRelationshipCode() {
        return relationshipCode;
    }

    public void setRelationshipCode(String relationshipCode) {
        this.relationshipCode = relationshipCode;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
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
