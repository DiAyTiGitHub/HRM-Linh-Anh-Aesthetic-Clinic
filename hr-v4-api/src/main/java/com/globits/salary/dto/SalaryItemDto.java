package com.globits.salary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryItem;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class SalaryItemDto extends BaseObjectDto {
    private String name;
    private String code; // Được sinh theo trường name. VD: name: Lương cơ bản -> code: LUONG_CO_BAN
    private Integer type; // Tính chất của thành phần lương: HrConstants.SalaryItemType
    private Boolean isTaxable; // Thành phần lương này có chịu thuế hay không
    private Boolean isInsurable; // Thành phần lương này có tính BHXH hay không
    private Boolean isActive; // Đang có hiệu lực hay không. VD: = false => Không thể chọn sử dụng thành phần
    // này cho mẫu bảng lương mới nữa
    private Double maxValue; // Mức trần/giá trị tối đa thành phần lương này có thể đạt
    private String defaultValue; // Gía trị mac dinh
    // private Boolean isSystemDefault; // Là hệ thống mặc định => Không thể cập
    // nhật/xóa thành phần lương là mặc định của hệ thống. VD: Lương cơ bản -> Mặc
    // định lấy theo hợp đồng đang có hiệu lực của nhân viên
    private Integer calculationType; // Cách tính giá trị của thành phần lương này:
    // HrConstants.SalaryItemCalculationType
    private String formula; // Công thức/Gía trị của thành phần này. VD: 10000 / LUONG_CO_BAN * 1.5
    private String description;
    // kiểu giá trị: thể hiện giá trị của cell thuộc kiểu gì, chi tiết xem
    // HrConstants.SalaryItemValueType
    private Integer valueType;

    // Các mức/ngưỡng của thành phần lương nếu có CalculationType là HrConstants.Threshold
//    private List<SalaryItemThresholdDto> thresholds;

    private UUID allowanceId;
    private String errorMessage;

    public SalaryItemDto() {

    }

    public SalaryItemDto(SalaryItem entity) {
        super();

        this.id = entity.getId();
        this.code = entity.getCode();
        this.name = entity.getName();
        this.type = entity.getType();
        this.isTaxable = entity.getIsTaxable();
        this.isInsurable = entity.getIsInsurable();
        this.isActive = entity.getIsActive();
        this.maxValue = entity.getMaxValue();
        this.calculationType = entity.getCalculationType();
        this.formula = entity.getFormula();
        this.description = entity.getDescription();
        this.valueType = entity.getValueType();
        this.defaultValue = entity.getDefaultValue();
        if (entity.getAllowance() != null && entity.getAllowance().getId() != null) {
            this.allowanceId = entity.getAllowance().getId();
        }

    }

    public SalaryItemDto(SalaryItem entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

//        this.thresholds = new ArrayList<>();
//        if (entity.getThresholds() != null && !entity.getThresholds().isEmpty()) {
//            for (SalaryItemThreshold threshold : entity.getThresholds()) {
//                SalaryItemThresholdDto thresholdItem = new SalaryItemThresholdDto(threshold);
//                this.thresholds.add(thresholdItem);
//            }
//        }
//
//        Collections.sort(this.thresholds, new Comparator<SalaryItemThresholdDto>() {
//            @Override
//            public int compare(SalaryItemThresholdDto o1, SalaryItemThresholdDto o2) {
//                // First, compare by displayOrder
//                if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
//                    return 0;
//                if (o1.getDisplayOrder() == null)
//                    return 1;
//                if (o2.getDisplayOrder() == null)
//                    return -1;
//
//                return o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
//            }
//        });

    }

    // used for setting up data
    public SalaryItemDto(String code, String name, String description, Integer type, Boolean isTaxable,
                         Boolean isInsurable, Boolean isActive, Double maxValue, Integer calculationType, String formula, Integer valueType) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.type = type;
        this.isTaxable = isTaxable;
        this.isActive = isActive;
        this.isInsurable = isInsurable;
        this.maxValue = maxValue;
        this.calculationType = calculationType;
        this.formula = formula;
        this.valueType = valueType;
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

//    public List<SalaryItemThresholdDto> getThresholds() {
//        return thresholds;
//    }
//
//    public void setThresholds(List<SalaryItemThresholdDto> thresholds) {
//        this.thresholds = thresholds;
//    }

    public UUID getAllowanceId() {
        return allowanceId;
    }

    public void setAllowanceId(UUID allowanceId) {
        this.allowanceId = allowanceId;
    }

    public Boolean getTaxable() {
        return isTaxable;
    }

    public void setTaxable(Boolean taxable) {
        isTaxable = taxable;
    }

    public Boolean getInsurable() {
        return isInsurable;
    }

    public void setInsurable(Boolean insurable) {
        isInsurable = insurable;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
