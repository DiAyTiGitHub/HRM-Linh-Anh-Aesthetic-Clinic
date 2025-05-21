package com.globits.hr.dto;

import com.globits.core.domain.BaseObject;
import com.globits.core.dto.BaseObjectDto;

import java.util.Date;

public class ImportFamilyRelationshipDto extends BaseObjectDto {
    private String staffCode; // mã nhân viên
    private String staffName; // tên nhân viên
    private String name; // Tên cha
    private Date dateOfBirth;//Ngày tháng năm sinh
    private String codeProfesstion; // mã nghề nghiệp
    private String nameProfesstion; //Nghề nghiệp
    private String codeRelationship;//Mã quan hệ
    private String nameRelationship;//Quan hệ

    public ImportFamilyRelationshipDto() {
    }

    public ImportFamilyRelationshipDto(BaseObject entity) {
        super(entity);
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCodeProfesstion() {
        return codeProfesstion;
    }

    public void setCodeProfesstion(String codeProfesstion) {
        this.codeProfesstion = codeProfesstion;
    }

    public String getNameProfesstion() {
        return nameProfesstion;
    }

    public void setNameProfesstion(String nameProfesstion) {
        this.nameProfesstion = nameProfesstion;
    }

    public String getCodeRelationship() {
        return codeRelationship;
    }

    public void setCodeRelationship(String codeRelationship) {
        this.codeRelationship = codeRelationship;
    }

    public String getNameRelationship() {
        return nameRelationship;
    }

    public void setNameRelationship(String nameRelationship) {
        this.nameRelationship = nameRelationship;
    }
}
