package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.StaffAllowance;

import java.util.Date;

public class StaffAllowanceDto extends BaseObjectDto {

	private StaffDto staff;
	private AllowanceDto allowance;
	private AllowancePolicyDto allowancePolicy;
	
	private Date startDate;
	private Date endDate;
	private String usingFormula;
	
	public StaffAllowanceDto() {
		
	}
	
    public StaffAllowanceDto(StaffAllowance entity) {
		this.id = entity.getId();
		if (entity.getStaff() != null) {
			this.staff = new StaffDto(entity.getStaff(), false, false );
		}
		if (entity.getAllowance() != null) {
			this.allowance = new AllowanceDto(entity.getAllowance());
		}
		if (entity.getAllowancePolicy() != null) {
			this.allowancePolicy = new AllowancePolicyDto(entity.getAllowancePolicy());
		}
		this.startDate = entity.getStartDate();
		this.endDate = entity.getEndDate();
		this.usingFormula = entity.getUsingFormula();
	}

	public StaffDto getStaff() {
		return staff;
	}

	public void setStaff(StaffDto staff) {
		this.staff = staff;
	}

	public AllowanceDto getAllowance() {
		return allowance;
	}

	public void setAllowance(AllowanceDto allowance) {
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

	public AllowancePolicyDto getAllowancePolicy() {
		return allowancePolicy;
	}

	public void setAllowancePolicy(AllowancePolicyDto allowancePolicy) {
		this.allowancePolicy = allowancePolicy;
	}
    
}
