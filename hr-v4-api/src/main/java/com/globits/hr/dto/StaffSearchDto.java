package com.globits.hr.dto;

import com.globits.core.dto.DepartmentDto;

public class StaffSearchDto {

    private DepartmentDto department;

    private String keyword;

    private Boolean isMainPosition;
    private Integer approvalStatus;

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public DepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDto department) {
        this.department = department;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Boolean getIsMainPosition() {
        return isMainPosition;
    }

    public void setIsMainPosition(Boolean isMainPosition) {
        this.isMainPosition = isMainPosition;
    }
}
