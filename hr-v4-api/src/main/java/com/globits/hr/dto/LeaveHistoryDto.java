package com.globits.hr.dto;

import java.util.Date;
import java.util.UUID;

public class LeaveHistoryDto {
	private UUID staffId;
	private Date startDate; // Ngày tạm nghỉ
    private Date endDate; // Ngày quay lại làm
    private String reason; // Lý do
    
    public LeaveHistoryDto() {
    	
    }
    
    public LeaveHistoryDto(UUID staffId, Date startDate,Date endDate,String reason) {
    	this.staffId = staffId;
    	this.startDate = startDate;
    	this.endDate = endDate;
    	this.reason = reason;
    }
    
	public UUID getStaffId() {
		return staffId;
	}
	public void setStaffId(UUID staffId) {
		this.staffId = staffId;
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
    
    
}
