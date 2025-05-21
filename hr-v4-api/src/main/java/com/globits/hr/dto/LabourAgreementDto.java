package com.globits.hr.dto;

import java.util.Date;
import java.util.UUID;

public class LabourAgreementDto {

    private UUID staffId; // Mã nhân viên
    private String nameOrg; // Mã Công ty ký HĐ
    private String codeOrg; // Công ty ký HĐ
    private String labourAgreementNumber; // Số HĐ (TV/HV)
    private Date startDate; // Ngày bắt đầu (HV/TV)
    private Date endDate; // Ngày kết thúc (HV/TV)
    private Integer totalDays; // Tổng số ngày
    private Double insuranceSalary;
    private String contactTypeName;
    private Date signDate;
    private Double salary;

    // Constructor đầy đủ tham số, đã thêm staffId
    public LabourAgreementDto(UUID staffId,
                              String nameOrg,
                              String codeOrg,
                              String labourAgreementNumber,
                              Date startDate,
                              Date endDate,
                              Integer totalDays,
                              Double insuranceSalary,
                              String contactTypeName,
                              Date signDate,
                              Double salary
    ) {
        this.staffId = staffId;
        this.nameOrg = nameOrg;
        this.codeOrg = codeOrg;
        this.labourAgreementNumber = labourAgreementNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.insuranceSalary = insuranceSalary;
        this.contactTypeName = contactTypeName;
        this.signDate = signDate;
        this.salary = salary;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getContactTypeName() {
        return contactTypeName;
    }

    public void setContactTypeName(String contactTypeName) {
        this.contactTypeName = contactTypeName;
    }

    public Double getInsuranceSalary() {
        return insuranceSalary;
    }

    public void setInsuranceSalary(Double insuranceSalary) {
        this.insuranceSalary = insuranceSalary;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getNameOrg() {
        return nameOrg;
    }

    public void setNameOrg(String nameOrg) {
        this.nameOrg = nameOrg;
    }

    public String getCodeOrg() {
        return codeOrg;
    }

    public void setCodeOrg(String codeOrg) {
        this.codeOrg = codeOrg;
    }

    public String getLabourAgreementNumber() {
        return labourAgreementNumber;
    }

    public void setLabourAgreementNumber(String labourAgreementNumber) {
        this.labourAgreementNumber = labourAgreementNumber;
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

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }
}
