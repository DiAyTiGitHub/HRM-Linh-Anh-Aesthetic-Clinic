package com.globits.salary.dto.search;

import com.globits.core.dto.SearchDto;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.dto.SalaryPeriodDto;

import java.util.List;
import java.util.UUID;

public class SearchStaffAdvancePaymentDto extends SearchDto {
    private HrOrganizationDto organization;
    private UUID organizationId;
    private HRDepartmentDto department;
    private UUID departmentId;
    private PositionTitleDto positionTitle;
    private UUID positionTitleId;

    private SalaryPeriodDto salaryPeriod;
    private UUID salaryPeriodId;

    private StaffDto staff;
    private UUID staffId;

    private Integer approvalStatus;
    private List<UUID> chosenRecordIds;


    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public UUID getSalaryPeriodId() {
        return salaryPeriodId;
    }

    public void setSalaryPeriodId(UUID salaryPeriodId) {
        this.salaryPeriodId = salaryPeriodId;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public List<UUID> getChosenRecordIds() {
        return chosenRecordIds;
    }

    public void setChosenRecordIds(List<UUID> chosenRecordIds) {
        this.chosenRecordIds = chosenRecordIds;
    }
}
