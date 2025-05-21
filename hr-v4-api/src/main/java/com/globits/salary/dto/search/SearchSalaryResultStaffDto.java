package com.globits.salary.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultDto;
import jakarta.persistence.Column;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class SearchSalaryResultStaffDto extends SearchDto {
    private UUID staffId;

    private UUID organizationId;
    private HrOrganizationDto organization;
    private UUID departmentId;
    private HRDepartmentDto department;
    private UUID positionTitleId;
    private PositionTitleDto positionTitle;
    private UUID positionId;

    private UUID salaryPeriodId;
    private UUID salaryResultId;
    private UUID salaryTemplateId;

    private List<UUID> staffs;

    private SalaryResultDto salaryResult;
    private SalaryPeriodDto salaryPeriod;
    private StaffDto staff;
    private Integer approvalStatus; // Trạng thái duyệt phiếu lương. Chi tiết trong:
    // HrConstants.SalaryResulStaffApprovalStatus
    private Integer paidStatus; // Trạng thái chi trả phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffPaidStatus

    private List<UUID> chosenPayslipIds;
    private List<UUID> salaryResultStaffIds;

    private Boolean isPayslip;

    public SearchSalaryResultStaffDto() {

    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
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

    public UUID getSalaryTemplateId() {
        return salaryTemplateId;
    }

    public void setSalaryTemplateId(UUID salaryTemplateId) {
        this.salaryTemplateId = salaryTemplateId;
    }

    public List<UUID> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<UUID> staffs) {
        this.staffs = staffs;
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

    public List<UUID> getChosenPayslipIds() {
        return chosenPayslipIds;
    }

    public void setChosenPayslipIds(List<UUID> chosenPayslipIds) {
        this.chosenPayslipIds = chosenPayslipIds;
    }

    public List<UUID> getSalaryResultStaffIds() {
        return salaryResultStaffIds;
    }

    public void setSalaryResultStaffIds(List<UUID> salaryResultStaffIds) {
        this.salaryResultStaffIds = salaryResultStaffIds;
    }

    public Boolean getIsPayslip() {
        return isPayslip;
    }

    public void setIsPayslip(Boolean isPayslip) {
        this.isPayslip = isPayslip;
    }

}
