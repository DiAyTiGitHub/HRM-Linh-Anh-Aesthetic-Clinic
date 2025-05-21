package com.globits.budget.dto.budget;

import com.globits.core.dto.SearchDto;

import java.util.Date;

public class BudgetSummaryDto extends SearchDto {
    private BudgetDto budget;
    private Double totalAmount;
    private Integer voucherType;
    private int month;  // Month for the summary
    private int year;  // Year for the summary
    private Date fromDate;
    private Date toDate;

    public BudgetSummaryDto() {
    }

    public BudgetSummaryDto(Double totalAmount, int voucherType, int month, int year) {
        this.totalAmount = totalAmount;
        this.voucherType = voucherType;
        this.month = month;
        this.year = year;
    }

    public BudgetSummaryDto(Double totalAmount, int voucherType, Date toDate) {
        this.totalAmount = totalAmount;
        this.voucherType = voucherType;
        this.toDate = toDate;
    }

    public BudgetSummaryDto(Double totalAmount, int voucherType, Date fromDate, Date toDate) {
        this.totalAmount = totalAmount;
        this.voucherType = voucherType;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    public BudgetDto getBudget() {
        return budget;
    }

    public void setBudget(BudgetDto budget) {
        this.budget = budget;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Integer getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(Integer voucherType) {
        this.voucherType = voucherType;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
