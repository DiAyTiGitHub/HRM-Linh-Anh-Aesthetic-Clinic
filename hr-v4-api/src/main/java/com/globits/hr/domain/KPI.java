package com.globits.hr.domain;

import com.globits.budget.domain.BaseNameCodeObject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Table(name = "tbl_kpi")
@Entity
public class KPI extends BaseNameCodeObject {
	private static final long serialVersionUID = 991992518344617174L;

	@OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<KPIItem> kpiItems;

	// Dùng tính tổng giá trị của các kpiItems (hiện tại là cộng tất cả)
	// Giống formula tính lương
	private String formula;

	public KPI() {
	}

	public Set<KPIItem> getKpiItems() {
		return kpiItems;
	}

	public void setKpiItems(Set<KPIItem> kpiItems) {
		this.kpiItems = kpiItems;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

}
