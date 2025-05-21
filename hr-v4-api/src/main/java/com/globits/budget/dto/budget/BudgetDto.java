package com.globits.budget.dto.budget;

import com.globits.budget.domain.Budget;
import com.globits.budget.dto.BaseNameCodeObjectDto;

public class BudgetDto extends BaseNameCodeObjectDto {
    private String currency;//Loại tiền tệ: VND, $, EUR, CNY...
    private Double openingBalance;//Số dư đầu kỳ
    private Double endingBalance;//Số dư cuối kỳ

    public BudgetDto() {
    }

    public BudgetDto(Budget entity) {
        super(entity);
        if (entity != null) {
            this.currency = entity.getCurrency();
            this.openingBalance = entity.getOpeningBalance();
            this.endingBalance = entity.getEndingBalance();
        }
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(Double endingBalance) {
        this.endingBalance = endingBalance;
    }
}
