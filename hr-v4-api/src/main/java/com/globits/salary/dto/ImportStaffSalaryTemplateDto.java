package com.globits.salary.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImportStaffSalaryTemplateDto {
	private Integer index;
	private String staffCode;
	private String staffName;
	private String salaryTemplateCode;
	private String salaryTemplateName;
	private Date startDate;
	private Date endDate;
	private Map<String, String> mapSalaryTemplateItem = new HashMap<>();
	//private String error; // lưu lỗi nếu có
	
	public ImportStaffSalaryTemplateDto() {
		
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getStaffCode() {
		return staffCode;
	}
	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public String getSalaryTemplateCode() {
		return salaryTemplateCode;
	}
	public void setSalaryTemplateCode(String salaryTemplateCode) {
		this.salaryTemplateCode = salaryTemplateCode;
	}
	public String getSalaryTemplateName() {
		return salaryTemplateName;
	}
	public void setSalaryTemplateName(String salaryTemplateName) {
		this.salaryTemplateName = salaryTemplateName;
	}
	public Map<String, String> getMapSalaryTemplateItem() {
		return mapSalaryTemplateItem;
	}
	public void setMapSalaryTemplateItem(Map<String, String> mapSalaryTemplateItem) {
		this.mapSalaryTemplateItem = mapSalaryTemplateItem;
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
	
}
