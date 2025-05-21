package com.globits.hr.domain;

import com.globits.budget.domain.BaseNameCodeObject;
import jakarta.persistence.*;

@Table(name = "tbl_kpi_item")
@Entity
public class KPIItem extends BaseNameCodeObject {
    @Column(name = "weight")
    private Double weight; // Tỷ lệ

    @Column(name = "used_for_salary")
    private Boolean usedForSalary;

    @ManyToOne
    @JoinColumn(name = "kpi_id")
    private KPI kpi;
    
	// viết hàm execute formula để trả về salaryResultStaffItem
	// Chỉ có value/result * giá trị * trọng số
    private String formula;

    public KPIItem() {
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Boolean getUsedForSalary() {
        return usedForSalary;
    }

    public void setUsedForSalary(Boolean usedForSalary) {
        this.usedForSalary = usedForSalary;
    }

    public KPI getKpi() {
        return kpi;
    }

    public void setKpi(KPI kpi) {
        this.kpi = kpi;
    }

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
    
    
}
