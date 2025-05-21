package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.timesheet.domain.Journal;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

// Kỳ lương/kỳ công
@Table(name = "tbl_salary_period")
@Entity
public class SalaryPeriod extends BaseObject {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "name")
	private String name; // ky cong/ky luong

	@Column(name = "code")
	private String code; // ma ky cong/ky luong

	@Column(name = "from_date")
	private Date fromDate;

	@Column(name = "to_date")
	private Date toDate;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description; // mo ta them = mo ta ky luong

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_period_id")
	private SalaryPeriod parentPeriod; // thuộc kỳ lương nào. VD: tháng 9 chi trả 2 kì lương => có 2 kì lương con

	@OneToMany(mappedBy = "parentPeriod", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SalaryPeriod> subPeriods; // các kỳ lương con thuộc kì lương này

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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SalaryPeriod getParentPeriod() {
		return parentPeriod;
	}

	public void setParentPeriod(SalaryPeriod parentPeriod) {
		this.parentPeriod = parentPeriod;
	}

	public Set<SalaryPeriod> getSubPeriods() {
		return subPeriods;
	}

	public void setSubPeriods(Set<SalaryPeriod> subPeriods) {
		this.subPeriods = subPeriods;
	}
}
