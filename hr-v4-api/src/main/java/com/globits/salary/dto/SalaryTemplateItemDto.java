package com.globits.salary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.AllowanceDto;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.SalaryTemplateItemConfig;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class SalaryTemplateItemDto extends BaseObjectDto {
    private Integer displayOrder; // Thứ tự hiển thị
    private String displayName; // Tên cột hiển thị trên bảng lương
    private String usingFormula; // Công thức thực tế sử dụng để tính toán giá trị của hàng trong cột. Mặc định
    // sẽ lấy theo formula của salaryItem. \n Người dùng có thể chỉnh sửa công thức
    // nếu cột này có salaryItem có trường calculationType = 'Dùng công thức'
    private UUID salaryTemplateId; // thuộc mẫu bang luong nao
    private UUID templateItemGroupId; // thuộc nhóm cot nao
    private SalaryItemDto salaryItem; // là thành phần lương nào
    private String description;
    private Boolean hiddenOnPayslip; // thành phần này sẽ bị ẩn trong phiếu lương
    private Boolean hiddenOnSalaryBoard; // thành phần này sẽ bị ẩn trong bảng lương

    private String code;
    private Integer type;
    private Boolean isTaxable;
    private Boolean isInsurable;

    private String defaultValue;
    private Double maxValue;
    private Integer calculationType;
    private Integer valueType;
    // private String ancillaryDescription;
    // private String usingFormula;
    private String formula; // Công thức/Gía trị của thành phần này. VD: 10000 / LUONG_CO_BAN * 1.5

    private Double value; // sử dụng để lưu trữ tạm thời value cho salaryvalue của staff và không có trong
    // cột của db
    private AllowanceDto allowance;

    private List<SalaryTemplateItemConfigDto> templateItemConfigs;

    private int colSpan;
    private Boolean isItem;

    public SalaryTemplateItemDto() {

    }

    public SalaryTemplateItemDto(SalaryTemplateItem entity) {
        super();

        this.id = entity.getId();
        this.displayOrder = entity.getDisplayOrder();
        this.displayName = entity.getDisplayName();
        this.hiddenOnPayslip = entity.getHiddenOnPayslip();
        this.hiddenOnSalaryBoard = entity.getHiddenOnSalaryBoard();
        this.formula = entity.getFormula();

        this.code = entity.getCode();
        this.type = entity.getType();
        this.isTaxable = entity.getIsTaxable();
        this.isInsurable = entity.getIsInsurable();
        this.defaultValue = entity.getDefaultValue();
        this.maxValue = entity.getMaxValue();
        this.calculationType = entity.getCalculationType();
        this.valueType = entity.getValueType();
        this.usingFormula = entity.getUsingFormula();

        if (entity.getSalaryItem() != null) {
            this.salaryItem = new SalaryItemDto(entity.getSalaryItem());
        }

        if (entity.getSalaryTemplate() != null) {
            this.salaryTemplateId = entity.getSalaryTemplate().getId();
        }

        if (entity.getTemplateItemGroup() != null) {
            this.templateItemGroupId = entity.getTemplateItemGroup().getId();
        }

        if (entity.getAllowance() != null) {
            this.allowance = new AllowanceDto();
            this.allowance.setId(entity.getAllowance().getId());
            this.allowance.setCode(entity.getAllowance().getCode());
            this.allowance.setName(entity.getAllowance().getName());
        }

        this.templateItemConfigs = new ArrayList<>();
    }

    public SalaryTemplateItemDto(SalaryTemplateItem entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

        this.description = entity.getDescription();

        if (entity.getTemplateItemConfigs() != null && !entity.getTemplateItemConfigs().isEmpty()) {
            List<SalaryTemplateItemConfigDto> itemConfigs = new ArrayList<>();

            for (SalaryTemplateItemConfig itemConfig : entity.getTemplateItemConfigs()) {
                SalaryTemplateItemConfigDto itemConfigDto = new SalaryTemplateItemConfigDto(itemConfig, true);
                itemConfigs.add(itemConfigDto);
            }

            itemConfigs.sort((o1, o2) -> {
                // So sánh compareOrder trước
                if (o1.getCompareOrder() == null && o2.getCompareOrder() == null) return 0;
                if (o1.getCompareOrder() == null) return 1;
                if (o2.getCompareOrder() == null) return -1;

                int compareOrderResult = o1.getCompareOrder().compareTo(o2.getCompareOrder());
                if (compareOrderResult != 0) return compareOrderResult;

                // So sánh minValue (ưu tiên null lên đầu)
                if (o1.getMinValue() == null && o2.getMinValue() != null) return -1;
                if (o1.getMinValue() != null && o2.getMinValue() == null) return 1;
                if (o1.getMinValue() != null && o2.getMinValue() != null) {
                    int minCompare = o1.getMinValue().compareTo(o2.getMinValue());
                    if (minCompare != 0) return minCompare;
                }

                // So sánh maxValue (ưu tiên null lên đầu)
                if (o1.getMaxValue() == null && o2.getMaxValue() != null) return -1;
                if (o1.getMaxValue() != null && o2.getMaxValue() == null) return 1;
                if (o1.getMaxValue() != null && o2.getMaxValue() != null) {
                    return o1.getMaxValue().compareTo(o2.getMaxValue());
                }

                return 0; // nếu bằng nhau
            });


            this.templateItemConfigs = itemConfigs;
        } else {
            this.templateItemConfigs = new ArrayList<>();
        }
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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

    public String getUsingFormula() {
        return usingFormula;
    }

    public void setUsingFormula(String usingFormula) {
        this.usingFormula = usingFormula;
    }

    public UUID getSalaryTemplateId() {
        return salaryTemplateId;
    }

    public void setSalaryTemplateId(UUID salaryTemplateId) {
        this.salaryTemplateId = salaryTemplateId;
    }

    public UUID getTemplateItemGroupId() {
        return templateItemGroupId;
    }

    public void setTemplateItemGroupId(UUID templateItemGroupId) {
        this.templateItemGroupId = templateItemGroupId;
    }

    public SalaryItemDto getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItemDto salaryItem) {
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

    public List<SalaryTemplateItemConfigDto> getTemplateItemConfigs() {
        return templateItemConfigs;
    }

    public void setTemplateItemConfigs(List<SalaryTemplateItemConfigDto> templateItemConfigs) {
        this.templateItemConfigs = templateItemConfigs;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public AllowanceDto getAllowance() {
        return allowance;
    }

    public void setAllowance(AllowanceDto allowance) {
        this.allowance = allowance;
    }

    public Boolean getHiddenOnSalaryBoard() {
        return hiddenOnSalaryBoard;
    }

    public void setHiddenOnSalaryBoard(Boolean hiddenOnSalaryBoard) {
        this.hiddenOnSalaryBoard = hiddenOnSalaryBoard;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
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

    public Boolean getItem() {
        return isItem;
    }

    public void setItem(Boolean item) {
        isItem = item;
    }
}
