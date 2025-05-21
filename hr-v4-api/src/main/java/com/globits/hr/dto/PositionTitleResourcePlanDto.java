package com.globits.hr.dto;

import java.util.UUID;

public class PositionTitleResourcePlanDto {
	private UUID departmentId;
	private String departmentName;
	private UUID positionTitleId;
	private String positionTitleName;
	private Integer nominalQuantity; // Số lượng định biên
	private Integer actualQuantity; // Số lượng thực tế
	private Integer supplementaryQuantity; // Cần bổ sung = định biên - thực tế
	private Integer filteredQuantity =0; // Cần lọc

	public PositionTitleResourcePlanDto() {
	}

	public String getPositionTitleName() {
		return positionTitleName;
	}

	public void setPositionTitleName(String positionTitleName) {
		this.positionTitleName = positionTitleName;
	}

	public Integer getNominalQuantity() {
		return nominalQuantity;
	}

	public void setNominalQuantity(Integer nominalQuantity) {
		this.nominalQuantity = nominalQuantity;
	}

	public Integer getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public Integer getSupplementaryQuantity() {
		return supplementaryQuantity;
	}

	public void setSupplementaryQuantity(Integer supplementaryQuantity) {
		this.supplementaryQuantity = supplementaryQuantity;
	}

	public Integer getFilteredQuantity() {
		return filteredQuantity;
	}

	public void setFilteredQuantity(Integer filteredQuantity) {
		this.filteredQuantity = filteredQuantity;
	}

	public UUID getPositionTitleId() {
		return positionTitleId;
	}

	public void setPositionTitleId(UUID positionTitleId) {
		this.positionTitleId = positionTitleId;
	}

	public UUID getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(UUID departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

}
