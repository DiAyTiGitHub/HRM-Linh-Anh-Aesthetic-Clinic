package com.globits.budget.dto;

import com.globits.budget.domain.BudgetCategory;

public class BudgetCategoryDto extends BaseNameCodeObjectDto {
    private String code;//Mã loại khoản
    private String icon;//Icon loại khoản, nếu cần thiết

    public BudgetCategoryDto(BudgetCategory entity) {
        super(entity);
        if (entity != null) {
            this.code = entity.getCode();
            this.icon = entity.getIcon();
        }
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public BudgetCategoryDto() {
        super();
    }
}
