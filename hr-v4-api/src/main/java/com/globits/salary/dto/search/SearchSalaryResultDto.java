package com.globits.salary.dto.search;

import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SearchSalaryResultDto extends SearchDto {
    private UUID staffId;

    private UUID organizationId;
    private UUID departmentId;
    private UUID positionTitleId;
    private UUID positionId;

    private UUID salaryPeriodId;
    private UUID salaryResultId;
    private UUID salaryTemplateId;

    private Integer status;

    private List<StaffDto> staffs;

    public SearchSalaryResultDto() {

    }

    public UUID getSalaryTemplateId() {
        return salaryTemplateId;
    }

    public void setSalaryTemplateId(UUID salaryTemplateId) {
        this.salaryTemplateId = salaryTemplateId;
    }

    @Override
    public UUID getStaffId() {
        return staffId;
    }

    @Override
    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public List<StaffDto> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<StaffDto> staffs) {
        this.staffs = staffs;
    }

    @Override
    public UUID getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public UUID getDepartmentId() {
        return departmentId;
    }

    @Override
    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    @Override
    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    @Override
    public UUID getPositionId() {
        return positionId;
    }

    @Override
    public void setPositionId(UUID positionId) {
        this.positionId = positionId;
    }

    public UUID getSalaryPeriodId() {
        return salaryPeriodId;
    }

    public void setSalaryPeriodId(UUID salaryPeriodId) {
        this.salaryPeriodId = salaryPeriodId;
    }

    public UUID getSalaryResultId() {
        return salaryResultId;
    }

    public void setSalaryResultId(UUID salaryResultId) {
        this.salaryResultId = salaryResultId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
