package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_salary_template_item_config")
public class SalaryTemplateItemConfig extends BaseObject {
    @ManyToOne
    @JoinColumn(name = "item_id")
    private SalaryTemplateItem templateItem;



    @Column(name = "compare_order")
    private Integer compareOrder; // Thứ tự thực hiện so sánh. VD: Tìm mức ngưỡng từ bé đến lớn, lấy mức ngưỡng đầu tiên đạt

    /*
     * Loai Config
     */
    @Column(name = "config_type")
    private Integer configType; // cố định hoặc công thức HrConstants.ConfigType

    @Column(name = "item_value")
    private Double itemValue; // Giá trị được sử dụng khi đạt mức ngưỡng

    @Column(name = "formula")
    private String formula; // Công thức được sử dụng khi đạt mức ngưỡng

    // Toán tử để so sánh cho giá trị cận dưới trong khoảng
    @Column(name = "operator_min_value")
    private Integer operatorMinValue; // toán tử so sánh. Chi tiết: HrConstants.SalaryTemplateItemConfigOperator

    // Giá trị cận dưới
    @Column(name = "min_value")
    private Double minValue;

    // Toán tử để so sánh cho giá trị cận trên trong khoảng
    @Column(name = "operator_max_value")
    private Integer operatorMaxValue; // toán tử so sánh. Chi tiết: HrConstants.SalaryTemplateItemConfigOperator

    // Giá trị cận trên
    @Column(name = "max_value")
    private Double maxValue;


    public SalaryTemplateItem getTemplateItem() {
        return templateItem;
    }

    public void setTemplateItem(SalaryTemplateItem templateItem) {
        this.templateItem = templateItem;
    }

    public Integer getCompareOrder() {
        return compareOrder;
    }

    public void setCompareOrder(Integer compareOrder) {
        this.compareOrder = compareOrder;
    }

    public Integer getConfigType() {
        return configType;
    }

    public void setConfigType(Integer configType) {
        this.configType = configType;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Integer getOperatorMinValue() {
        return operatorMinValue;
    }

    public void setOperatorMinValue(Integer operatorMinValue) {
        this.operatorMinValue = operatorMinValue;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Integer getOperatorMaxValue() {
        return operatorMaxValue;
    }

    public void setOperatorMaxValue(Integer operatorMaxValue) {
        this.operatorMaxValue = operatorMaxValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }
}
