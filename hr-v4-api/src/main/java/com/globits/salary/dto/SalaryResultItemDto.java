package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.*;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

// Thành phần lương trong bảng lương
public class SalaryResultItemDto extends BaseObjectDto {
    private Integer displayOrder; // Thứ tự hiển thị
    private String displayName; // Tên cột hiển thị trên bảng lương
    private UUID salaryResultId; // là thành phần lương trong bảng lương nào
    private UUID resultItemGroupId; // thuộc nhóm cot nao
    private SalaryItemDto salaryItem; // là thành phần lương nào

    // Công thức thực tế sử dụng để tính toán giá trị của hàng trong cột.
    // Mặc định sẽ lấy theo formula của salaryTemplateItem.
    // Người dùng có thể chỉnh sửa công thức nếu cột này có salaryItem có trường
    // calculationType = 'Dùng công thức'
    private String usingFormula;

    // các trường dưới đây được copy từ thành phần lương gốc
    private String code; // Được sinh theo trường name. VD: name: Lương cơ bản -> code: LUONG_CO_BAN
    private Integer type; // Tính chất của thành phần lương: HrConstants.SalaryItemType
    private Boolean isTaxable; // Thành phần lương này có chịu thuế hay không
    private Boolean isInsurable; // Thành phần lương này có tính BHXH hay không
    private Double maxValue; // Mức trần/giá trị tối đa thành phần lương này có thể đạt
    private String defaultValue; // Mức trần/giá trị tối đa thành phần lương này có thể đạt
    private Integer calculationType; // Cách tính giá trị của thành phần lương này:
    // HrConstants.SalaryItemCalculationType
    private Integer valueType; // kiểu giá trị: thể hiện giá trị của cell thuộc kiểu gì, chi tiết xem
    // HrConstants.SalaryItemValueType
    private String description;
    private Boolean hiddenOnPayslip; // thành phần này sẽ bị ẩn trong phiếu lương


    public SalaryResultItemDto() {

    }

    public SalaryResultItemDto(SalaryResultItem entity) {
        super();

        this.id = entity.getId();
        this.displayOrder = entity.getDisplayOrder();
        this.displayName = entity.getDisplayName();
        this.usingFormula = entity.getUsingFormula();
        // riêng trường 'code' chỉ cho xem, không cho nhập mới/ghi đè
        this.code = entity.getCode();
        this.hiddenOnPayslip = entity.getHiddenOnPayslip();

        if (entity.getSalaryItem() != null) {
            this.salaryItem = new SalaryItemDto(entity.getSalaryItem());
        }

        if (entity.getSalaryResult() != null) {
            this.salaryResultId = entity.getSalaryResult().getId();
        }

        if (entity.getSalaryResultItemGroup() != null) {
            this.resultItemGroupId = entity.getSalaryResultItemGroup().getId();
        }
    }

    public SalaryResultItemDto(SalaryResultItem entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

        this.description = entity.getDescription();

        this.type = entity.getType();
        this.isTaxable = entity.getIsTaxable();
        this.isInsurable = entity.getIsInsurable();
        this.maxValue = entity.getMaxValue();
        this.calculationType = entity.getCalculationType();
        this.valueType = entity.getValueType();
        this.defaultValue = entity.getDefaultValue();
    }


    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UUID getSalaryResultId() {
        return salaryResultId;
    }

    public void setSalaryResultId(UUID salaryResultId) {
        this.salaryResultId = salaryResultId;
    }

    public UUID getResultItemGroupId() {
        return resultItemGroupId;
    }

    public void setResultItemGroupId(UUID resultItemGroupId) {
        this.resultItemGroupId = resultItemGroupId;
    }

    public SalaryItemDto getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItemDto salaryItem) {
        this.salaryItem = salaryItem;
    }

    public String getUsingFormula() {
        return usingFormula;
    }

    public void setUsingFormula(String usingFormula) {
        this.usingFormula = usingFormula;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getIsTaxable() {
        return isTaxable;
    }

    public void setIsTaxable(Boolean isTaxable) {
        this.isTaxable = isTaxable;
    }

    public Boolean getIsInsurable() {
        return isInsurable;
    }

    public void setIsInsurable(Boolean isInsurable) {
        this.isInsurable = isInsurable;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(Integer calculationType) {
        this.calculationType = calculationType;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getHiddenOnPayslip() {
        return hiddenOnPayslip;
    }

    public void setHiddenOnPayslip(Boolean hiddenOnPayslip) {
        this.hiddenOnPayslip = hiddenOnPayslip;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
