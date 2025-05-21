package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

@Table(name = "tbl_kpi_result_item")
@Entity
public class KPIResultItem extends BaseObject {

    @ManyToOne
    @JoinColumn(name = "kpi_result_id")
    private KPIResult kpiResult;

    @ManyToOne
    @JoinColumn(name = "kpi_item_id")
    private KPIItem kpiItem;

    @Column(name = "target")
    private Double target; // Chỉ số Mục tiêu

    @Column(name = "value")
    private Double value; // Kết quả

    public KPIResultItem() {
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public KPIResult getKpiResult() {
        return kpiResult;
    }

    public void setKpiResult(KPIResult kpiResult) {
        this.kpiResult = kpiResult;
    }

    public KPIItem getKpiItem() {
        return kpiItem;
    }

    public void setKpiItem(KPIItem kpiItem) {
        this.kpiItem = kpiItem;
    }

	public Double getTarget() {
		return target;
	}

	public void setTarget(Double target) {
		this.target = target;
	}
    
    
}
