package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Allowance;
import jakarta.persistence.*;

import java.util.Set;

// Thành phần lương trong bảng lương
@Table(name = "tbl_salary_result_item")
@Entity
public class SalaryResultItem extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "display_order")
    private Integer displayOrder; // Thứ tự hiển thị
    @Column(name = "display_name")
    private String displayName; // Tên cột hiển thị trên bảng lương

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_result_id")
    private SalaryResult salaryResult; // là thành phần lương trong bảng lương nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_result_item_group")
    private SalaryResultItemGroup salaryResultItemGroup; // thuộc nhóm cot nao

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_item_id")
    private SalaryItem salaryItem; // là thành phần lương nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "copied_template_item_id")
    private SalaryTemplateItem copiedTemplateItem; // được copy từ thành phần lương trong mẫu bảng lương nào

    // các trường dưới đây được copy từ thành phần lương gốc
    private String code; // Được sinh theo trường name. VD: name: Lương cơ bản -> code: LUONG_CO_BAN
    private Integer type; // Tính chất của thành phần lương: HrConstants.SalaryItemType
    @Column(name = "is_taxable")
    private Boolean isTaxable; // Thành phần lương này có chịu thuế hay không
    @Column(name = "is_insurable")
    private Boolean isInsurable; // Thành phần lương này có tính BHXH hay không
    @Column(name = "default_value")
    private String defaultValue; // Gía trị mac dinh
    @Column(name = "max_value")
    private Double maxValue; // Mức trần/giá trị tối đa thành phần lương này có thể đạt
    @Column(name = "calculation_type")
    private Integer calculationType; // Cách tính giá trị của thành phần lương này: HrConstants.SalaryItemCalculationType
    @Column(name = "value_type")
    private Integer valueType; // kiểu giá trị: thể hiện giá trị của cell thuộc kiểu gì, chi tiết xem HrConstants.SalaryItemValueType
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Công thức thực tế sử dụng để tính toán giá trị của hàng trong cột.
    // Mặc định sẽ lấy theo formula của salaryTemplateItem.
    // Người dùng có thể chỉnh sửa công thức nếu cột này có salaryItem có trường calculationType = 'Dùng công thức'
    @Column(name = "using_formula", columnDefinition = "TEXT")
    private String usingFormula;

    @Column(name = "hidden_on_payslip")
    private Boolean hiddenOnPayslip; // thành phần này sẽ bị ẩn trong phiếu lương

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allowance_id")
    private Allowance allowance; // thành phần lương của cột này là phụ cấp nào

    @OneToMany(mappedBy = "salaryResultItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SalaryResultStaffItem> salaryResultStaffItems; //  Các cell trong cột


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

    public SalaryResult getSalaryResult() {
        return salaryResult;
    }

    public void setSalaryResult(SalaryResult salaryResult) {
        this.salaryResult = salaryResult;
    }

    public SalaryResultItemGroup getSalaryResultItemGroup() {
        return salaryResultItemGroup;
    }

    public void setSalaryResultItemGroup(SalaryResultItemGroup salaryResultItemGroup) {
        this.salaryResultItemGroup = salaryResultItemGroup;
    }

    public SalaryItem getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItem salaryItem) {
        this.salaryItem = salaryItem;
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

    public String getUsingFormula() {
        return usingFormula;
    }

    public void setUsingFormula(String usingFormula) {
        this.usingFormula = usingFormula;
    }

    public SalaryTemplateItem getCopiedTemplateItem() {
        return copiedTemplateItem;
    }

    public void setCopiedTemplateItem(SalaryTemplateItem copiedTemplateItem) {
        this.copiedTemplateItem = copiedTemplateItem;
    }

    public Boolean getHiddenOnPayslip() {
        return hiddenOnPayslip;
    }

    public void setHiddenOnPayslip(Boolean hiddenOnPayslip) {
        this.hiddenOnPayslip = hiddenOnPayslip;
    }

    public Set<SalaryResultStaffItem> getSalaryResultStaffItems() {
        return salaryResultStaffItems;
    }

    public void setSalaryResultStaffItems(Set<SalaryResultStaffItem> salaryResultStaffItems) {
        this.salaryResultStaffItems = salaryResultStaffItems;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Allowance getAllowance() {
        return allowance;
    }

    public void setAllowance(Allowance allowance) {
        this.allowance = allowance;
    }
}
