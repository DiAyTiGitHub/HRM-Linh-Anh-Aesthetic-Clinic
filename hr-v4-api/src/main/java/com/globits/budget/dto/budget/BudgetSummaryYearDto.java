package com.globits.budget.dto.budget;

public class BudgetSummaryYearDto {
    private int month;
    private Double income;
    private Double expenditure;

    public BudgetSummaryYearDto() {
    }

    public BudgetSummaryYearDto(int month, Double income, Double expenditure) {
        this.month = month;
        this.income = income;
        this.expenditure = expenditure;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(Double expenditure) {
        this.expenditure = expenditure;
    }
}