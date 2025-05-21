package com.globits.hr.domain;

import com.globits.budget.domain.BaseNameCodeObject;
import com.globits.salary.domain.SalaryPeriod;
import jakarta.persistence.*;

import java.util.Date;

// Thu nhập/Khấu trừ khác
@Table(name = "tbl_other_income")
@Entity
public class OtherIncome extends BaseNameCodeObject {

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "salary_period_id")
    private SalaryPeriod salaryPeriod; // thu nhập vào kỳ lương nào

    @Column(name = "income")
    private Double income;

    @Column(name = "note")
    private String note;

    @Column(name = "type")
    private Integer type; //HrConstants.OtherIncomeType

    @Column(name = "decision_date")
    private Date decisionDate; // ngày quyết định

    public OtherIncome() {
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public SalaryPeriod getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriod salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }
}
