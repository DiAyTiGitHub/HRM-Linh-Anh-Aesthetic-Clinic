package com.globits.hr.dto.function;

import java.util.Date;

public class StaffFamilyRelationshipFunctionDto {
    private String staffCode;
    private String familyRelationship;
    private String fullName;
    private String address;
    private Date birthDate;

    public String getStaffCode() {
        return staffCode;
    }

    public StaffFamilyRelationshipFunctionDto() {

    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFamilyRelationship() {
        return familyRelationship;
    }

    public void setFamilyRelationship(String familyRelationship) {
        this.familyRelationship = familyRelationship;
    }

    @Override
    public String toString() {
        return "StaffFamilyRelationshipFunctionDto{" +
                "staffCode='" + staffCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
