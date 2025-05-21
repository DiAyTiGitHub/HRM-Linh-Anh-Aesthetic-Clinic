package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.salary.domain.SalaryUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
/*
 * Loại phụ cấp
 */
@Entity
@Table(name = "tbl_allowance_type")

public class AllowanceType extends BaseObject {
    @Column(name = "name")
    private String name;
    
    @Column(name = "code")
    private String code;

    @Column(name = "other_name")
    private String otherName;

    @Column(name = "default_value")
    private Double defaultValue;// muc huong mac dinh

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_value_unit_id")
    private SalaryUnit defaultValueUnit;// don vi tinh

    @Column(name = "insurance_value")
    private Double insuranceValue;// muc tham gia bhxh

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insurance_value_unit_id")
    private SalaryUnit insuranceValueUnit;// don vi tinh

    @Column(name = "tax_reduction_value")
    private Double taxReductionValue;// muc mien giam thue

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tax_reduction_value_unit_id")
    private SalaryUnit taxReductionValueUnit;// don vi tinh

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

	public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public SalaryUnit getDefaultValueUnit() {
        return defaultValueUnit;
    }

    public void setDefaultValueUnit(SalaryUnit defaultValueUnit) {
        this.defaultValueUnit = defaultValueUnit;
    }

    public Double getInsuranceValue() {
        return insuranceValue;
    }

    public void setInsuranceValue(Double insuranceValue) {
        this.insuranceValue = insuranceValue;
    }

    public SalaryUnit getInsuranceValueUnit() {
        return insuranceValueUnit;
    }

    public void setInsuranceValueUnit(SalaryUnit insuranceValueUnit) {
        this.insuranceValueUnit = insuranceValueUnit;
    }

    public Double getTaxReductionValue() {
        return taxReductionValue;
    }

    public void setTaxReductionValue(Double taxReductionValue) {
        this.taxReductionValue = taxReductionValue;
    }

    public SalaryUnit getTaxReductionValueUnit() {
        return taxReductionValueUnit;
    }

    public void setTaxReductionValueUnit(SalaryUnit taxReductionValueUnit) {
        this.taxReductionValueUnit = taxReductionValueUnit;
    }
}
