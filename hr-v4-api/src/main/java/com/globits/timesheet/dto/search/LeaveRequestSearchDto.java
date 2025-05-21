package com.globits.timesheet.dto.search;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.dto.SalaryPeriodDto;

public class LeaveRequestSearchDto extends SearchDto {

    private UUID requestStaffId;
    private UUID approvalStaffId;
    private Integer approvalStatus;
    private Date fromRequestDate;
    private Date toRequestDate;
    private List<UUID> chosenIds;

    private Date fromDate;
    private Date toDate;

    private SalaryPeriodDto salaryPeriod;
    private UUID salaryPeriodId;
    private StaffDto staff;
    private HrOrganizationDto organization;
    private UUID organizationId;
    private HRDepartmentDto department;
    private UUID departmentId;
    private PositionTitleDto positionTitle;
    private UUID positionTitleId;

    public UUID getRequestStaffId() {
        return requestStaffId;
    }

    public void setRequestStaffId(UUID requestStaffId) {
        this.requestStaffId = requestStaffId;
    }

    public UUID getApprovalStaffId() {
        return approvalStaffId;
    }

    public void setApprovalStaffId(UUID approvalStaffId) {
        this.approvalStaffId = approvalStaffId;
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

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Date getFromRequestDate() {
        return fromRequestDate;
    }

    public void setFromRequestDate(Date fromRequestDate) {
        this.fromRequestDate = fromRequestDate;
    }

    public Date getToRequestDate() {
        return toRequestDate;
    }

    public void setToRequestDate(Date toRequestDate) {
        this.toRequestDate = toRequestDate;
    }

    public List<UUID> getChosenIds() {
        return chosenIds;
    }

    public void setChosenIds(List<UUID> chosenIds) {
        this.chosenIds = chosenIds;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public UUID getSalaryPeriodId() {
        return salaryPeriodId;
    }

    public void setSalaryPeriodId(UUID salaryPeriodId) {
        this.salaryPeriodId = salaryPeriodId;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    @Override
    public UUID getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    @Override
    public UUID getDepartmentId() {
        return departmentId;
    }

    @Override
    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    @Override
    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    @Override
    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }
}
