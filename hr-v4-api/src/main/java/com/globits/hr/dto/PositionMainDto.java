package com.globits.hr.dto;

import java.util.UUID;

public class PositionMainDto {
    private UUID staffId;
    private UUID positionId;
    
    private String positionName;
    private String positionCode;
    
    private String departmentName;
    private String departmentCode;

    private String positionTitleName;
    private String positionTitleCode;
    private String rankTitleName;

    private String supervisorName;
    private String supervisorCode;
    private String supervisorStaffCode;
    private String supervisorStaffDisplayName;
    
    private String positionTitleGroupCode;
    private String positionTitleGroupName;

    public PositionMainDto(UUID staffId,
                           UUID positionId,
                           String positionName,
                           String positionCode,
                           String departmentName,
                           String departmentCode,
                           String positionTitleName,
                           String positionTitleCode,
                           String rankTitleName,
                           String supervisorName,
                           String supervisorCode,
                           String supervisorStaffCode,
                           String supervisorStaffDisplayName,
                           String positionTitleGroupCode,
                           String positionTitleGroupName) {
        this.staffId = staffId;
        this.positionId = positionId;
        this.positionName = positionName;
        this.positionCode = positionCode;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.positionTitleName = positionTitleName;
        this.positionTitleCode = positionTitleCode;
        this.rankTitleName = rankTitleName;
        this.supervisorName = supervisorName;
        this.supervisorCode = supervisorCode;
        this.supervisorStaffCode = supervisorStaffCode;
        this.supervisorStaffDisplayName = supervisorStaffDisplayName;
        this.positionTitleGroupCode = positionTitleGroupCode;
        this.positionTitleGroupName = positionTitleGroupName;
        
    }

	public UUID getStaffId() {
		return staffId;
	}

	public void setStaffId(UUID staffId) {
		this.staffId = staffId;
	}

	public UUID getPositionId() {
		return positionId;
	}

	public void setPositionId(UUID positionId) {
		this.positionId = positionId;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getPositionCode() {
		return positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getPositionTitleName() {
		return positionTitleName;
	}

	public void setPositionTitleName(String positionTitleName) {
		this.positionTitleName = positionTitleName;
	}

	public String getPositionTitleCode() {
		return positionTitleCode;
	}

	public void setPositionTitleCode(String positionTitleCode) {
		this.positionTitleCode = positionTitleCode;
	}

	public String getRankTitleName() {
		return rankTitleName;
	}

	public void setRankTitleName(String rankTitleName) {
		this.rankTitleName = rankTitleName;
	}

	public String getSupervisorName() {
		return supervisorName;
	}

	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	public String getSupervisorCode() {
		return supervisorCode;
	}

	public void setSupervisorCode(String supervisorCode) {
		this.supervisorCode = supervisorCode;
	}

	public String getSupervisorStaffCode() {
		return supervisorStaffCode;
	}

	public void setSupervisorStaffCode(String supervisorStaffCode) {
		this.supervisorStaffCode = supervisorStaffCode;
	}

	public String getSupervisorStaffDisplayName() {
		return supervisorStaffDisplayName;
	}

	public void setSupervisorStaffDisplayName(String supervisorStaffDisplayName) {
		this.supervisorStaffDisplayName = supervisorStaffDisplayName;
	}

	public String getPositionTitleGroupCode() {
		return positionTitleGroupCode;
	}

	public void setPositionTitleGroupCode(String positionTitleGroupCode) {
		this.positionTitleGroupCode = positionTitleGroupCode;
	}

	public String getPositionTitleGroupName() {
		return positionTitleGroupName;
	}

	public void setPositionTitleGroupName(String positionTitleGroupName) {
		this.positionTitleGroupName = positionTitleGroupName;
	}


}
