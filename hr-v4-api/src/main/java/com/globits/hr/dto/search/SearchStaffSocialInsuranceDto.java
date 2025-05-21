package com.globits.hr.dto.search;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultDto;

import java.util.List;
import java.util.UUID;

public class SearchStaffSocialInsuranceDto extends SearchDto {
    private SalaryResultDto salaryResult;
    private SalaryPeriodDto salaryPeriod;
    private UUID salaryPeriodId;

    private StaffDto staff;
    private UUID staffId;

    private UUID organizationId;
    private HrOrganizationDto organization;
    private UUID contractOrganizationId;
    private HrOrganizationDto contractOrganization;
    private UUID departmentId;
    private HRDepartmentDto department;
    private UUID positionTitleId;
    private PositionTitleDto positionTitle;
    private UUID positionId;

    private Integer paidStatus;
    private List<UUID> chosenRecordIds;

    public UUID getSalaryPeriodId() {
        return salaryPeriodId;
    }

    public void setSalaryPeriodId(UUID salaryPeriodId) {
        this.salaryPeriodId = salaryPeriodId;
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

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    @Override
    public UUID getDepartmentId() {
        return departmentId;
    }

    @Override
    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    @Override
    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    @Override
    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    @Override
    public UUID getPositionId() {
        return positionId;
    }

    @Override
    public void setPositionId(UUID positionId) {
        this.positionId = positionId;
    }

    public SalaryResultDto getSalaryResult() {
        return salaryResult;
    }

    public void setSalaryResult(SalaryResultDto salaryResult) {
        this.salaryResult = salaryResult;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }

    public List<UUID> getChosenRecordIds() {
        return chosenRecordIds;
    }

    public void setChosenRecordIds(List<UUID> chosenRecordIds) {
        this.chosenRecordIds = chosenRecordIds;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public UUID getContractOrganizationId() {
        return contractOrganizationId;
    }

    public void setContractOrganizationId(UUID contractOrganizationId) {
        this.contractOrganizationId = contractOrganizationId;
    }

    public HrOrganizationDto getContractOrganization() {
        return contractOrganization;
    }

    public void setContractOrganization(HrOrganizationDto contractOrganization) {
        this.contractOrganization = contractOrganization;
    }
}
