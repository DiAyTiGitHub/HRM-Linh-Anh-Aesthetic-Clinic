package com.globits.hr.dto.search;

import java.util.List;
import java.util.UUID;

public class SearchOtherIncomeDto extends SearchDto{
    private Integer type;
    private UUID salaryPeriodId;

    public SearchOtherIncomeDto() {
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public UUID getSalaryPeriodId() {
        return salaryPeriodId;
    }

    public void setSalaryPeriodId(UUID salaryPeriodId) {
        this.salaryPeriodId = salaryPeriodId;
    }
}
