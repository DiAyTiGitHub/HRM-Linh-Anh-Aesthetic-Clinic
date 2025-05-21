package com.globits.hr.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

/**
 * @author dunghq
 * Quá trình đóng bảo hiểm xã hội
 */

@Table(name = "tbl_staff_insurance_history")
@Entity
public class StaffInsuranceHistory extends BaseObject {
    private static final long serialVersionUID = 1L;
    @Column(name = "start_date")
    private Date startDate;//Ngày bắt đầu mức đóng

    @Column(name = "end_date")
    private Date endDate;//Ngày kết thúc mức đóng

    @Column(name = "note")
    private String note;//Ghi chú

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Column(name = "insurance_salary")
    private Double insuranceSalary;//Mức lương đóng bảo hiểm xã hội

    @Column(name = "staff_percentage")
    private Double staffPercentage;//Tỷ lệ cá nhân đóng bảo hiểm xã hội

    @Column(name = "org_percentage")
    private Double orgPercentage;//Tỷ lệ đơn vị đóng bảo hiểm xã hội

    @Column(name = "staff_insurance_amount")
    private Double staffInsuranceAmount;//Số tiền cá nhân đóng

    @Column(name = "org_insurance_amount")
    private Double orgInsuranceAmount;//Số tiền đơn vị đóng

    @Column(name = "social_insurance_book_code")
    private String socialInsuranceBookCode;//Số sổ bảo hiểm xã hội

    @Column(name = "profession_name")
    private String professionName;//Mã ngạch viên chức khi đóng BHCH --> Dữ liệu mang tính lịch sử, có thể không sử dụng sau này

    @Column(name = "department_name")
    private String departmentName;//Trực thuộc phòng ban nào khi đóng mức BHXH này --> Dữ liệu mang tính lịch sử, có thể không sử dụng sau này

    @Column(name = "allowance_coefficient")
    private Double allowanceCoefficient;//Hệ số phụ cấp --> Dữ liệu mang tính lịch sử, có thể không sử dụng sau này

    @Column(name = "salary_cofficient")
    private Double salaryCofficient;//Hệ số lương đóng bảo hiểm xã hội


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

    public Double getSalaryCofficient() {
        return salaryCofficient;
    }

    public void setSalaryCofficient(Double salaryCofficient) {
        this.salaryCofficient = salaryCofficient;
    }

    public Double getInsuranceSalary() {
        return insuranceSalary;
    }

    public void setInsuranceSalary(Double insuranceSalary) {
        this.insuranceSalary = insuranceSalary;
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

    public Double getStaffInsuranceAmount() {
        return staffInsuranceAmount;
    }

    public void setStaffInsuranceAmount(Double staffInsuranceAmount) {
        this.staffInsuranceAmount = staffInsuranceAmount;
    }

    public Double getOrgInsuranceAmount() {
        return orgInsuranceAmount;
    }

    public void setOrgInsuranceAmount(Double orgInsuranceAmount) {
        this.orgInsuranceAmount = orgInsuranceAmount;
    }

    public String getSocialInsuranceBookCode() {
        return socialInsuranceBookCode;
    }

    public void setSocialInsuranceBookCode(String socialInsuranceBookCode) {
        this.socialInsuranceBookCode = socialInsuranceBookCode;
    }

    public String getProfessionName() {
        return professionName;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Double getAllowanceCoefficient() {
        return allowanceCoefficient;
    }

    public void setAllowanceCoefficient(Double allowanceCoefficient) {
        this.allowanceCoefficient = allowanceCoefficient;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

}
