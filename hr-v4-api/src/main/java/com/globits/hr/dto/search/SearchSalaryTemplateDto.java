package com.globits.hr.dto.search;

public class SearchSalaryTemplateDto extends SearchDto {
    private Boolean isCreatePayslip;

    public Boolean getIsCreatePayslip() {
        return isCreatePayslip;
    }

    public void setIsCreatePayslip(Boolean isCreatePayslip) {
        this.isCreatePayslip = isCreatePayslip;
    }
}
