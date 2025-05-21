package com.globits.hr.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;
import com.globits.salary.domain.SalaryIncrementType;

/*
 * Bảng lịch sử lương - dùng cho lương ngạch bậc hiện tại
 */

@Table(name = "tbl_staff_salary_history")
@Entity
public class StaffSalaryHistory extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Column(name = "coofficient")
    private Double coefficient;//Hệ số lương

    @Column(name = "staff_type_code")
    private String staffTypeCode;//Mã loại nhân viên

    @Column(name = "coefficient_over_level")
    private Double coefficientOverLevel;//Tỷ lệ vượt khung

    @Column(name = "percentage")
    private Double percentage;//Tỷ lệ phần trăm hưởng (tỷ lệ 85% thử việc hoặc 70%, ...)

    @Column(name = "decision_date")
    private Date decisionDate;//Ngày quyết định tăng lương

    @Column(name = "decision_code")
    private String decisionCode;//Số quyêt định tăng lương

    @Column(name = "start_date")
    private Date startDate;//Ngày bắt đầu thụ hưởng

    @Column(name = "next_salary_increment_date")
    private Date nextSalaryIncrementDate;//Ngày dự kiến tăng lương tiếp theo

    @Column(name = "note")
    private String note;//Ghi chú

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_increment_type")
    private SalaryIncrementType salaryIncrementType;//Loại tăng lương

    @Column(name = "start_staff_type_code_date")
    private Date startStaffTypeCodeDate;//Ngày bắt đầu mã ngạch công chức tương ứng với mức lương --> Dữ liệu mang tính lịch sử

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getStaffTypeCode() {
        return staffTypeCode;
    }

    public void setStaffTypeCode(String staffTypeCode) {
        this.staffTypeCode = staffTypeCode;
    }

    public Double getCoefficientOverLevel() {
        return coefficientOverLevel;
    }

    public void setCoefficientOverLevel(Double coefficientOverLevel) {
        this.coefficientOverLevel = coefficientOverLevel;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getDecisionCode() {
        return decisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        this.decisionCode = decisionCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getNextSalaryIncrementDate() {
        return nextSalaryIncrementDate;
    }

    public void setNextSalaryIncrementDate(Date nextSalaryIncrementDate) {
        this.nextSalaryIncrementDate = nextSalaryIncrementDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public SalaryIncrementType getSalaryIncrementType() {
        return salaryIncrementType;
    }

    public void setSalaryIncrementType(SalaryIncrementType salaryIncrementType) {
        this.salaryIncrementType = salaryIncrementType;
    }

    public Date getStartStaffTypeCodeDate() {
        return startStaffTypeCodeDate;
    }

    public void setStartStaffTypeCodeDate(Date startStaffTypeCodeDate) {
        this.startStaffTypeCodeDate = startStaffTypeCodeDate;
    }
}
