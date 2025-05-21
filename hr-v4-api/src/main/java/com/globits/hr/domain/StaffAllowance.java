package com.globits.hr.domain;

import java.util.Date;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
/*
 *  Phụ cấp nhân viên
 */

@Entity
@Table(name = "tbl_staff_allowance")
public class StaffAllowance extends BaseObject {
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowance_id")
    private Allowance allowance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowance_policy_id")
    private AllowancePolicy allowancePolicy;

    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "using_formula")
    private String usingFormula;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public Allowance getAllowance() {
		return allowance;
	}

	public void setAllowance(Allowance allowance) {
		this.allowance = allowance;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getUsingFormula() {
		return usingFormula;
	}

	public void setUsingFormula(String usingFormula) {
		this.usingFormula = usingFormula;
	}

	public AllowancePolicy getAllowancePolicy() {
		return allowancePolicy;
	}

	public void setAllowancePolicy(AllowancePolicy allowancePolicy) {
		this.allowancePolicy = allowancePolicy;
	}
    
}
