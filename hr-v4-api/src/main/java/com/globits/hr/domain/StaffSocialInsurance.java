package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.SalaryResult;
import jakarta.persistence.*;

import java.util.Date;

/**
 * Quá trình đóng bảo hiểm xã hội
 * của nhân viên theo kì lương
 */

@Table(name = "tbl_staff_social_insurance")
@Entity
public class StaffSocialInsurance extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    // Mức lương tham gia bảo hiểm xã hội
    @Column(name = "insurance_salary")
    private Double insuranceSalary;

    // Tỷ lệ đóng BHXH của nhân viên
    @Column(name = "staff_si_percentage")
    private Double staffSocialInsurancePercentage;

    // Tỷ lệ đóng BHYT của nhân viên
    @Column(name = "staff_hi_percentage")
    private Double staffHealthInsurancePercentage;

    // Tỷ lệ đóng BHTN của nhân viên
    @Column(name = "staff_ui_percentage")
    private Double staffUnemploymentInsurancePercentage;

    // Tổng tiền bảo hiểm mà nhân viên đóng
    @Column(name = "staff_total_insurance")
    private Double staffTotalInsuranceAmount;

    // Tỷ lệ đóng BHXH của công ty
    @Column(name = "org_si_percentage")
    private Double orgSocialInsurancePercentage;

    // Tỷ lệ đóng BHYT của công ty
    @Column(name = "org_hi_percentage")
    private Double orgHealthInsurancePercentage;

    // Tỷ lệ đóng BHTN của công ty
    @Column(name = "org_ui_percentage")
    private Double orgUnemploymentInsurancePercentage;

    // Tổng tiền bảo hiểm mà công ty đóng
    @Column(name = "org_total_insurance")
    private Double orgTotalInsuranceAmount;

    @Column(name = "paid_status")
    private Integer paidStatus; // Bảo hiểm này của nhan vien da duoc tra (dong) hay chua. Chi tiet: HrConstants.StaffSocialInsurancePaidStatus

    @Column(name = "total_insurance_amount")
    private Double totalInsuranceAmount;
    
    @Column(name = "start_date")
    private Date startDate;//Ngày bắt đầu mức đóng

    @Column(name = "end_date")
    private Date endDate;//Ngày kết thúc mức đóng

    // Ghi chú
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_period_id")
    private SalaryPeriod salaryPeriod; // kỳ lương nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_result_id")
    private SalaryResult salaryResult;


    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Double getInsuranceSalary() {
        return insuranceSalary;
    }

    public void setInsuranceSalary(Double insuranceSalary) {
        this.insuranceSalary = insuranceSalary;
    }

    public Double getStaffSocialInsurancePercentage() {
        return staffSocialInsurancePercentage;
    }

    public void setStaffSocialInsurancePercentage(Double staffSocialInsurancePercentage) {
        this.staffSocialInsurancePercentage = staffSocialInsurancePercentage;
    }

    public Double getStaffHealthInsurancePercentage() {
        return staffHealthInsurancePercentage;
    }

    public void setStaffHealthInsurancePercentage(Double staffHealthInsurancePercentage) {
        this.staffHealthInsurancePercentage = staffHealthInsurancePercentage;
    }

    public Double getStaffUnemploymentInsurancePercentage() {
        return staffUnemploymentInsurancePercentage;
    }

    public void setStaffUnemploymentInsurancePercentage(Double staffUnemploymentInsurancePercentage) {
        this.staffUnemploymentInsurancePercentage = staffUnemploymentInsurancePercentage;
    }

    public Double getStaffTotalInsuranceAmount() {
        return staffTotalInsuranceAmount;
    }

    public void setStaffTotalInsuranceAmount(Double staffTotalInsuranceAmount) {
        this.staffTotalInsuranceAmount = staffTotalInsuranceAmount;
    }

    public Double getOrgSocialInsurancePercentage() {
        return orgSocialInsurancePercentage;
    }

    public void setOrgSocialInsurancePercentage(Double orgSocialInsurancePercentage) {
        this.orgSocialInsurancePercentage = orgSocialInsurancePercentage;
    }

    public Double getOrgHealthInsurancePercentage() {
        return orgHealthInsurancePercentage;
    }

    public void setOrgHealthInsurancePercentage(Double orgHealthInsurancePercentage) {
        this.orgHealthInsurancePercentage = orgHealthInsurancePercentage;
    }

    public Double getOrgUnemploymentInsurancePercentage() {
        return orgUnemploymentInsurancePercentage;
    }

    public void setOrgUnemploymentInsurancePercentage(Double orgUnemploymentInsurancePercentage) {
        this.orgUnemploymentInsurancePercentage = orgUnemploymentInsurancePercentage;
    }

    public Double getOrgTotalInsuranceAmount() {
        return orgTotalInsuranceAmount;
    }

    public void setOrgTotalInsuranceAmount(Double orgTotalInsuranceAmount) {
        this.orgTotalInsuranceAmount = orgTotalInsuranceAmount;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getTotalInsuranceAmount() {
        return totalInsuranceAmount;
    }

    public void setTotalInsuranceAmount(Double totalInsuranceAmount) {
        this.totalInsuranceAmount = totalInsuranceAmount;
    }

    public SalaryPeriod getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriod salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public SalaryResult getSalaryResult() {
        return salaryResult;
    }

    public void setSalaryResult(SalaryResult salaryResult) {
        this.salaryResult = salaryResult;
    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }
}
