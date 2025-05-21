package com.globits.hr.dto;

import java.util.List;
import java.util.UUID;

public class DepartmentResourcePlanDto  {
	 private UUID departmentId;
	 private String departmentName;
	 private List<PositionTitleResourcePlanDto> positionTitles;
	 private Integer nominalQuantity = 0;
	 private Integer actualQuantity = 0;
	 private Integer supplementaryQuantity = 0;
	 private Integer filteredQuantity = 0;
	 private List<DepartmentResourcePlanDto> children;
    public DepartmentResourcePlanDto() {
    }

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public List<PositionTitleResourcePlanDto> getPositionTitles() {
		return positionTitles;
	}

	public void setPositionTitles(List<PositionTitleResourcePlanDto> positionTitles) {
		this.positionTitles = positionTitles;
	}

	public UUID getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(UUID departmentId) {
		this.departmentId = departmentId;
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

	public List<DepartmentResourcePlanDto> getChildren() {
		return children;
	}

	public void setChildren(List<DepartmentResourcePlanDto> children) {
		this.children = children;
	}

}
