package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryConfigItem;

public class SalaryConfigItemDto extends BaseObjectDto {
    private SalaryConfigDto salaryConfig;
    private SalaryItemDto salaryItem;
    private int itemOrder;
    private String formula;

    public SalaryConfigItemDto() {

    }

    public SalaryConfigItemDto(SalaryConfigItem s) {// danh mục cấu hình lương
        this.setId(s.getId());
        this.setItemOrder(s.getItemOrder());
        this.setFormula(s.getFormula());
        if (s.getSalaryItem() != null) {
            SalaryItemDto salaryItem = new SalaryItemDto();// phần tử lương
            salaryItem.setId(s.getSalaryItem().getId());
            salaryItem.setCode(s.getSalaryItem().getCode());
            salaryItem.setName(s.getSalaryItem().getName());
//            salaryItem.setIsDefault(s.getSalaryItem().getIsDefault());
            salaryItem.setFormula(s.getSalaryItem().getFormula());
            salaryItem.setIsActive(s.getSalaryItem().getIsActive());
            salaryItem.setType(s.getSalaryItem().getType());
            this.salaryItem = salaryItem;
        }

        if (s.getSalaryConfig() != null) {// cấu hình lương
            SalaryConfigDto salaryConfig = new SalaryConfigDto();
            salaryConfig.setId(s.getId());
            salaryConfig.setCode(s.getSalaryConfig().getCode());
            salaryConfig.setName(s.getSalaryConfig().getName());
            this.salaryConfig = salaryConfig;
        }
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }

    public SalaryConfigDto getSalaryConfig() {
        return salaryConfig;
    }

    public void setSalaryConfig(SalaryConfigDto salaryConfig) {
        this.salaryConfig = salaryConfig;
    }

    public SalaryItemDto getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItemDto salaryItem) {
        this.salaryItem = salaryItem;
    }

}
