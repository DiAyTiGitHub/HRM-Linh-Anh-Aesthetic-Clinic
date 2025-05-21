package com.globits.budget.dto.budget;

import java.util.List;

public class BudgetSummaryBalanceDto {
    private BudgetDto budget;
    private BudgetSummaryDto summaryUntilToDate; //Số tiền hiện tại của ngân sách tới ngày "To date"
    private List<BudgetSummaryDto> summaryFromDateToDate; //Tổng thu, chi của ngân sách từ ngày "To date" đến ngày "From date"

    public BudgetSummaryBalanceDto() {
    }

    public BudgetSummaryBalanceDto(BudgetDto budget, BudgetSummaryDto summaryUntilToDate, List<BudgetSummaryDto> summaryFromDateToDate) {
        this.budget = budget;
        this.summaryUntilToDate = summaryUntilToDate;
        this.summaryFromDateToDate = summaryFromDateToDate;
    }

    public BudgetDto getBudget() {
        return budget;
    }

    public void setBudget(BudgetDto budget) {
        this.budget = budget;
    }

    public BudgetSummaryDto getSummaryUntilToDate() {
        return summaryUntilToDate;
    }

    public void setSummaryUntilToDate(BudgetSummaryDto summaryUntilToDate) {
        this.summaryUntilToDate = summaryUntilToDate;
    }

    public List<BudgetSummaryDto> getSummaryFromDateToDate() {
        return summaryFromDateToDate;
    }

    public void setSummaryFromDateToDate(List<BudgetSummaryDto> summaryFromDateToDate) {
        this.summaryFromDateToDate = summaryFromDateToDate;
    }
}
