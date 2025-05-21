package com.globits.hr.domain;

import java.util.Date;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Chi phí giới thiệu
@Entity
@Table(name = "tbl_hr_introduce_cost")
public class HrIntroduceCost extends BaseObject {
	private static final long serialVersionUID = -2208752009903206352L;

	@ManyToOne
	@JoinColumn(name = "staff_id")
	private Staff staff; // Nhân viên giới thiệu

	@ManyToOne
	@JoinColumn(name = "introduced_staff_id")
	private Staff introducedStaff; // Nhân viên được giới thiệu

	@Column(name = "period_order")
	private Integer periodOrder; // thứ tự hiển thị cột tháng tính giới thiệu đợt

	@Column(name = "introduce_period")
	private Date introducePeriod; // Ngày tính giới thiệu đợt 1

	@Column(name = "cost")
	private Double cost; // Chi phí nhân viên giới thiệu được hưởng đợt 1

	@Column(name = "introduce_period_2")
	private Date introducePeriod2; // Ngày tính giới thiệu đợt 2

	@Column(name = "cost_2")
	private Double cost2; // Chi phí nhân viên giới thiệu được hưởng đợt 2

	@Column(name = "introduce_period_3")
	private Date introducePeriod3; // Ngày tính giới thiệu đợt 3

	@Column(name = "cost_3")
	private Double cost3; // Chi phí nhân viên giới thiệu được hưởng đợt 3

	@Column(name = "note")
	private String note; // Ghi chú


	public Staff getIntroducedStaff() {
		return introducedStaff;
	}

	public void setIntroducedStaff(Staff introducedStaff) {
		this.introducedStaff = introducedStaff;
	}

	public Date getIntroducePeriod2() {
		return introducePeriod2;
	}

	public void setIntroducePeriod2(Date introducePeriod2) {
		this.introducePeriod2 = introducePeriod2;
	}

	public Double getCost2() {
		return cost2;
	}

	public void setCost2(Double cost2) {
		this.cost2 = cost2;
	}

	public Date getIntroducePeriod3() {
		return introducePeriod3;
	}

	public void setIntroducePeriod3(Date introducePeriod3) {
		this.introducePeriod3 = introducePeriod3;
	}

	public Double getCost3() {
		return cost3;
	}

	public void setCost3(Double cost3) {
		this.cost3 = cost3;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public Date getIntroducePeriod() {
		return introducePeriod;
	}

	public void setIntroducePeriod(Date introducePeriod) {
		this.introducePeriod = introducePeriod;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getPeriodOrder() {
		return periodOrder;
	}

	public void setPeriodOrder(Integer periodOrder) {
		this.periodOrder = periodOrder;
	}
	
	
}
