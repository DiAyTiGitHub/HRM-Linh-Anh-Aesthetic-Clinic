package com.globits.timesheet.dto.search;

import java.util.Date;

public class SearchPublicHolidayDateDto {
	private Integer pageIndex;
	private Integer pageSize;
	private Date fromDate;
	private Date toDate;
	private Integer holidayType;
	
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
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
	public Integer getHolidayType() {
		return holidayType;
	}
	public void setHolidayType(Integer holidayType) {
		this.holidayType = holidayType;
	}
	
	

}
