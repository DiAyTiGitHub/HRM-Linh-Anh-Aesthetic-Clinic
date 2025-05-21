package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryTemplateItemConfig;

import java.util.UUID;

public class SalaryTemplateItemConfigDto extends BaseObjectDto {
    private UUID templateItemId;

    private Integer compareOrder; // Thứ tự thực hiện so sánh. VD: Tìm mức ngưỡng từ bé đến lớn, lấy mức ngưỡng đầu tiên đạt
    private Integer configType; // cố định hoặc công thức HrConstants.ConfigType
    private Double itemValue; // Giá trị được sử dụng khi đạt mức ngưỡng
    private String formula; // Công thức được sử dụng khi đạt mức ngưỡng
    private Integer operatorMinValue; // toán tử so sánh. Chi tiết: HrConstants.SalaryTemplateItemConfigOperator
    private Double minValue;
    private Integer operatorMaxValue; // toán tử so sánh. Chi tiết: HrConstants.SalaryTemplateItemConfigOperator
    private Double maxValue;

    public SalaryTemplateItemConfigDto() {

    }

    public SalaryTemplateItemConfigDto(SalaryTemplateItemConfig entity) {
        super();

        this.itemValue = entity.getItemValue();
        this.configType = entity.getConfigType();
        this.formula = entity.getFormula();
        this.compareOrder = entity.getCompareOrder();

        this.operatorMaxValue = entity.getOperatorMaxValue();
        this.operatorMinValue = entity.getOperatorMinValue();
        this.minValue = entity.getMinValue();
        this.maxValue = entity.getMaxValue();

        if (entity.getTemplateItem() != null) {
            this.templateItemId = entity.getTemplateItem().getId();
        }
    }

    public SalaryTemplateItemConfigDto(SalaryTemplateItemConfig entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

    }


    public UUID getTemplateItemId() {
        return templateItemId;
    }

    public void setTemplateItemId(UUID templateItemId) {
        this.templateItemId = templateItemId;
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
