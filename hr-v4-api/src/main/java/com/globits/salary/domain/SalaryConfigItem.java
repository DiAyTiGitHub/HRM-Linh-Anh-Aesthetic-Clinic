package com.globits.salary.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;


@Table(name = "tbl_salary_config_item")
@Entity
public class SalaryConfigItem extends BaseObject {
    private static final long serialVersionUID = 5044965188766036345L;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_config_id")
    private SalaryConfig salaryConfig;

    @Column(name = "item_order")
    private int itemOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_item_id")
    private SalaryItem salaryItem;

    @Column(name = "formula")
    private String formula;

    public SalaryConfig getSalaryConfig() {
        return salaryConfig;
    }

    public void setSalaryConfig(SalaryConfig salaryConfig) {
        this.salaryConfig = salaryConfig;
    }

    public SalaryItem getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItem salaryItem) {
        this.salaryItem = salaryItem;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
