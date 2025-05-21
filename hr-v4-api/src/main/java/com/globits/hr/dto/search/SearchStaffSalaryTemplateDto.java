package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchStaffSalaryTemplateDto extends SearchDto {
    private UUID staffSalaryTemplateId;
    private UUID salaryTemplateId;

    private UUID staffId;

    private UUID organizationId;
    private UUID departmentId;
    private UUID positionTitleId;
    private UUID positionId;

    public UUID getStaffSalaryTemplateId() {
        return staffSalaryTemplateId;
    }

    public void setStaffSalaryTemplateId(UUID staffSalaryTemplateId) {
        this.staffSalaryTemplateId = staffSalaryTemplateId;
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
}
