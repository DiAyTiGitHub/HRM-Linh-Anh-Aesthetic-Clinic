package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Allowance;
import com.globits.hr.domain.StaffSocialInsurance;
import jakarta.persistence.*;

import java.util.Set;

// Thành phần thuộc bảng lương => Là các cột trong bảng lương
@Table(name = "tbl_salary_template_item")
@Entity
public class SalaryTemplateItem extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "display_order")
    private Integer displayOrder; // Thứ tự hiển thị

    @Column(name = "display_name")
    private String displayName; // Tên cột hiển thị trên bảng lương

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_template_id")
    private SalaryTemplate salaryTemplate; // thuộc mẫu bang luong nao

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_template_item_group")
    private SalaryTemplateItemGroup templateItemGroup; // thuộc nhóm cot nao

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_item_id")
    private SalaryItem salaryItem; // là thành phần lương nào

    @Column(name = "hidden_on_payslip")
    private Boolean hiddenOnPayslip; // thành phần này sẽ bị ẩn trong phiếu lương

    @Column(name = "hidden_on_salary_board")
    private Boolean hiddenOnSalaryBoard; // thành phần này sẽ bị ẩn trong bảng lương

    @Column(name = "hidden_on_excel_report")
    private Boolean hiddenOnExcelReport; // thành phần này sẽ bị ẩn khi xuất báo cáo excel

    // bo sung cac truong muc dich nhan ban mau bang luong
    @Column(name = "code")
    private String code;

    @Column(name = "type")
    private Integer type;

    @Column(name = "is_taxable")
    private Boolean isTaxable;

    @Column(name = "is_insurable")
    private Boolean isInsurable;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "calculation_type")
    private Integer calculationType;

    @Column(name = "value_type")
    private Integer valueType;

    //@Column(name = "ancillary_description")
    //private String ancillaryDescription;

    @Column(name = "using_formula", columnDefinition = "TEXT")
    private String usingFormula; //không dùng nữa

    @Column(name = "formula", columnDefinition = "TEXT")
    private String formula; // nếu type là USING_FORMULA thì lưu công thức, nếu type là THRESHOLD thì lưu code của item để so sánh ngưỡng

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allowance_id")
    private Allowance allowance; // thành phần lương của cột này là phụ cấp nào

    @OneToMany(mappedBy = "templateItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SalaryTemplateItemConfig> templateItemConfigs;

    @OneToMany(mappedBy = "templateItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffSalaryItemValue> staffSalaryItemValues;

    @OneToMany(mappedBy = "salaryTemplateItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SalaryResultStaffItem> salaryResultStaffItems;


    public Boolean getHiddenOnExcelReport() {
        return hiddenOnExcelReport;
    }

    public void setHiddenOnExcelReport(Boolean hiddenOnExcelReport) {
        this.hiddenOnExcelReport = hiddenOnExcelReport;
    }

    public Set<SalaryResultStaffItem> getSalaryResultStaffItems() {
        return salaryResultStaffItems;
    }

    public void setSalaryResultStaffItems(Set<SalaryResultStaffItem> salaryResultStaffItems) {
        this.salaryResultStaffItems = salaryResultStaffItems;
    }

    public Set<StaffSalaryItemValue> getStaffSalaryItemValues() {
        return staffSalaryItemValues;
    }

    public void setStaffSalaryItemValues(Set<StaffSalaryItemValue> staffSalaryItemValues) {
        this.staffSalaryItemValues = staffSalaryItemValues;
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

    public SalaryTemplate getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplate salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public SalaryTemplateItemGroup getTemplateItemGroup() {
        return templateItemGroup;
    }

    public void setTemplateItemGroup(SalaryTemplateItemGroup templateItemGroup) {
        this.templateItemGroup = templateItemGroup;
    }

    public SalaryItem getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItem salaryItem) {
        this.salaryItem = salaryItem;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public String getUsingFormula() {
        return usingFormula;
    }

    public void setUsingFormula(String usingFormula) {
        this.usingFormula = usingFormula;
    }

    public Set<SalaryTemplateItemConfig> getTemplateItemConfigs() {
        return templateItemConfigs;
    }

    public void setTemplateItemConfigs(Set<SalaryTemplateItemConfig> templateItemConfigs) {
        this.templateItemConfigs = templateItemConfigs;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Boolean getInsurable() {
        return isInsurable;
    }

    public void setInsurable(Boolean insurable) {
        isInsurable = insurable;
    }

    public Boolean getTaxable() {
        return isTaxable;
    }

    public void setTaxable(Boolean taxable) {
        isTaxable = taxable;
    }

    public Allowance getAllowance() {
        return allowance;
    }

    public void setAllowance(Allowance allowance) {
        this.allowance = allowance;
    }

    public Boolean getHiddenOnSalaryBoard() {
        return hiddenOnSalaryBoard;
    }

    public void setHiddenOnSalaryBoard(Boolean hiddenOnSalaryBoard) {
        this.hiddenOnSalaryBoard = hiddenOnSalaryBoard;
    }
}
