package com.globits.hr.dto;

import com.globits.hr.domain.AllowanceType;
import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.dto.SalaryUnitDto;

public class AllowanceTypeDto extends BaseObjectDto {
    private String name;
    private String code;
    private String otherName;
    private Double defaultValue;// muc huong mac dinh
    private SalaryUnitDto defaultValueUnit;
    private Double insuranceValue;// muc tham gia bhxh
    private SalaryUnitDto insuranceValueUnit;// don vi tinh
    private Double taxReductionValue;// muc mien giam thue
    private SalaryUnitDto taxReductionValueUnit;// don vi tinh
    
    public AllowanceTypeDto() {

    }

    public AllowanceTypeDto(AllowanceType entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.otherName = entity.getOtherName();
        this.defaultValue = entity.getDefaultValue();
        this.insuranceValue = entity.getInsuranceValue();
        this.taxReductionValue = entity.getTaxReductionValue();
        this.taxReductionValue = entity.getTaxReductionValue();

        if (entity.getInsuranceValueUnit() != null) {
            this.insuranceValueUnit = new SalaryUnitDto(entity.getInsuranceValueUnit());
        }
        if (entity.getTaxReductionValueUnit() != null) {
            this.taxReductionValueUnit = new SalaryUnitDto(entity.getTaxReductionValueUnit());
        }
        if (entity.getDefaultValueUnit() != null) {
            this.defaultValueUnit = new SalaryUnitDto(entity.getDefaultValueUnit());
        }
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public void setDefaultValue(Double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDefaultValueUnit(SalaryUnitDto defaultValueUnit) {
        this.defaultValueUnit = defaultValueUnit;
    }

    public SalaryUnitDto getTaxReductionValueUnit() {
        return taxReductionValueUnit;
    }

    public void setTaxReductionValue(Double taxReductionValue) {
        this.taxReductionValue = taxReductionValue;
    }

    public void setTaxReductionValueUnit(SalaryUnitDto taxReductionValueUnit) {
        this.taxReductionValueUnit = taxReductionValueUnit;
    }

    public Double getTaxReductionValue() {
        return taxReductionValue;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherName() {
        return otherName;
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    public SalaryUnitDto getDefaultValueUnit() {
        return defaultValueUnit;
    }

    public Double getInsuranceValue() {
        return insuranceValue;
    }

    public void setInsuranceValue(Double insuranceValue) {
        this.insuranceValue = insuranceValue;
    }

    public SalaryUnitDto getInsuranceValueUnit() {
        return insuranceValueUnit;
    }

    public void setInsuranceValueUnit(SalaryUnitDto insuranceValueUnit) {
        this.insuranceValueUnit = insuranceValueUnit;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
    
}
