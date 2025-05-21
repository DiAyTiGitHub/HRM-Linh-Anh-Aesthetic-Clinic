package com.globits.hr.dto;

import com.globits.budget.domain.BaseNameCodeObject;
import com.globits.budget.dto.BaseNameCodeObjectDto;
import com.globits.hr.domain.OtherIncome;
import com.globits.hr.domain.Staff;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.dto.SalaryPeriodDto;
import jakarta.persistence.*;

import java.util.Date;

// Thu nhập khác
public class OtherIncomeDto extends BaseNameCodeObjectDto {
    private StaffDto staff;
    private SalaryPeriodDto salaryPeriod;
    private Double income;
    private String note;
    private Integer type;
    private Date decisionDate;

    public OtherIncomeDto() {
    }

    public OtherIncomeDto(OtherIncome entity) {
        super(entity);
        if (entity != null) {
            this.income = entity.getIncome();
            this.note = entity.getNote();
            this.type = entity.getType();
            this.decisionDate = entity.getDecisionDate();
            if (entity.getStaff() != null) {
                this.staff = new StaffDto(entity.getStaff(), false, false);
            }
            if (entity.getSalaryPeriod() != null) {
                this.salaryPeriod = new SalaryPeriodDto(entity.getSalaryPeriod(), false);
            }
        }
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
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
