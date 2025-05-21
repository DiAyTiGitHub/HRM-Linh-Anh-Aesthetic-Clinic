package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Set;

@Table(name = "tbl_kpi_result")
@Entity
public class KPIResult extends BaseObject {
	@ManyToOne
	@JoinColumn(name = "staff_id")
	private Staff staff;

	@ManyToOne
	@JoinColumn(name = "kpi_id")
	private KPI kpi;

	@OneToMany(mappedBy = "kpiResult", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<KPIResultItem> kpiResultItems;

	// tổng hiệu suất theo kỳ (salary period)
	// Gía trị hiệu suất cuối cùng
	@Column(name = "final_value")
	private Double finalValue;

	public KPIResult() {
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public KPI getKpi() {
		return kpi;
	}

	public void setKpi(KPI kpi) {
		this.kpi = kpi;
	}

	public Set<KPIResultItem> getKpiResultItems() {
		return kpiResultItems;
	}

	public void setKpiResultItems(Set<KPIResultItem> kpiResultItems) {
		this.kpiResultItems = kpiResultItems;
	}
}
