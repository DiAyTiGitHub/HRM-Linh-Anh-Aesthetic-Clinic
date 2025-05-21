package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Allowance;
import jakarta.persistence.*;

import java.util.Set;

// Thành phần lương
@Table(name = "tbl_salary_item")
@Entity
public class SalaryItem extends BaseObject {
    private static final long serialVersionUID = 1L;

    private String name;
    private String code; // Được sinh theo trường name. VD: name: Lương cơ bản -> code: LUONG_CO_BAN
    private Integer type; // Tính chất của thành phần lương: HrConstants.SalaryItemType
    @Column(name = "is_taxable")
    private Boolean isTaxable; // Thành phần lương này có chịu thuế hay không
    @Column(name = "is_insurable")
    private Boolean isInsurable; // Thành phần lương này có tính BHXH hay không
    @Column(name = "is_active")
    private Boolean isActive; // Đang có hiệu lực hay không. VD: = false => Không thể chọn sử dụng thành phần này cho mẫu bảng lương mới nữa
    @Column(name = "max_value")
    private Double maxValue; // Mức trần/giá trị tối đa thành phần lương này có thể đạt
    @Column(name = "default_value")
    private String defaultValue; // Gía trị mac dinh
    @Column(name = "calculation_type")
    private Integer calculationType; // Cách tính giá trị của thành phần lương này: HrConstants.SalaryItemCalculationType
    @Column(name = "formula", columnDefinition = "TEXT")
    private String formula; // Công thức/Gía trị của thành phần này. VD: 10000 / LUONG_CO_BAN * 1.5
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // kiểu giá trị: thể hiện giá trị của cell thuộc kiểu gì, chi tiết xem HrConstants.SalaryItemValueType
    @Column(name = "value_type")
    private Integer valueType;

//    // Các mức/ngưỡng của thành phần lương nếu có CalculationType là HrConstants.Threshold
//    @OneToMany(mappedBy = "salaryItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<SalaryItemThreshold> thresholds;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowance_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_salary_item_allowance"))
    private Allowance allowance; // thành phần lương của cột này là phụ cấp nào

    // Là kết nối của cấu hình nào.
    // VD: Đây là thành phần lương Số giờ làm việc => Mỗi khi chấm công thì dữ liệu số giờ làm việc sẽ được cập nhật vào các phiếu lương sử dụng thành phần lương này
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_auto_map_id")
    private SalaryAutoMap salaryAutoMap;

    public SalaryAutoMap getSalaryAutoMap() {
        return salaryAutoMap;
    }

    public void setSalaryAutoMap(SalaryAutoMap salaryAutoMap) {
        this.salaryAutoMap = salaryAutoMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

//    public Set<SalaryItemThreshold> getThresholds() {
//        return thresholds;
//    }
//
//    public void setThresholds(Set<SalaryItemThreshold> thresholds) {
//        this.thresholds = thresholds;
//    }

    public Allowance getAllowance() {
        return allowance;
    }

    public void setAllowance(Allowance allowance) {
        this.allowance = allowance;
    }
}
