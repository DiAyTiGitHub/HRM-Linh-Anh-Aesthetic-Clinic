package com.globits.timesheet.dto;

public class IndexLeaveTypeDto {
	private String nameLeaveType;
	private String codeLeaveType;
	private Integer totalShifts;
	
	public IndexLeaveTypeDto() {
		
	}
	
	public String getNameLeaveType() {
		return nameLeaveType;
	}
	public void setNameLeaveType(String nameLeaveType) {
		this.nameLeaveType = nameLeaveType;
	}
	public String getCodeLeaveType() {
		return codeLeaveType;
	}
	public void setCodeLeaveType(String codeLeaveType) {
		this.codeLeaveType = codeLeaveType;
	}
	public Integer getTotalShifts() {
		return totalShifts;
	}
	public void setTotalShifts(Integer totalShifts) {
		this.totalShifts = totalShifts;
	}
	
	
	
}
