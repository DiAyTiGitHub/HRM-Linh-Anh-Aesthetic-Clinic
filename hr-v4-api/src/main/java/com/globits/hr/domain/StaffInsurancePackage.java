package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

/*
 *  Các bảo hiểm khác nhân viên tham gia
 */
@Entity
@Table(name = "tbl_staff_insurance_package")
public class StaffInsurancePackage extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_package_id")
    private InsurancePackage insurancePackage; // Đóng gói bảo hiểm nào

    @Column(name = "start_date")
    private Date startDate; // Ngày bắt đầu đóng bảo hiểm

    @Column(name = "end_date")
    private Date endDate; // Ngày kết thúc đóng bảo hiểm

    @Column(name = "insurance_amount")
    private Double insuranceAmount; // Mức tham gia bảo hiểm

    @Column(name = "compensation_amount")
    private Double compensationAmount; // Mức đền bù bảo hiểm

    @Column(name = "staff_percentage")
    private Double staffPercentage; // Tỷ lệ nhân viên đóng bảo hiểm

    @Column(name = "org_percentage")
    private Double orgPercentage; // Tỷ lệ công ty đóng bảo hiểm

    @Column(name = "has_family_participation")
    private Boolean hasFamilyParticipation; // Có đóng cho thân nhân người lao động


    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
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

    public Double getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(Double insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public Double getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(Double compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    public Double getStaffPercentage() {
        return staffPercentage;
    }

    public void setStaffPercentage(Double staffPercentage) {
        this.staffPercentage = staffPercentage;
    }

    public Double getOrgPercentage() {
        return orgPercentage;
    }

    public void setOrgPercentage(Double orgPercentage) {
        this.orgPercentage = orgPercentage;
    }

    public Boolean getHasFamilyParticipation() {
        return hasFamilyParticipation;
    }

    public void setHasFamilyParticipation(Boolean hasFamilyParticipation) {
        this.hasFamilyParticipation = hasFamilyParticipation;
    }
}
