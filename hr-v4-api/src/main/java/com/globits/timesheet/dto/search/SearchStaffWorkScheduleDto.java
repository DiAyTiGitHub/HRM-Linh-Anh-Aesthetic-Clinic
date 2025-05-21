package com.globits.timesheet.dto.search;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.dto.SalaryPeriodDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SearchStaffWorkScheduleDto extends SearchDto {
    private List<UUID> staffIds;
    private UUID staffId;
    private String staffCode;
    private List<String> staffCodes;
    private UUID coordinatorId;
    private UUID shiftWorkId;
    private Date workingDate;
    private Integer workingStatus;
    private UUID positionId;
    private Boolean isExportExcel = false;
    private UUID leaveTypeId;
    private Boolean isFutureDate;
    private Boolean isLeaveSchechual = false;
    private Date fromDate;
    private Date toDate;
    private SalaryPeriodDto salaryPeriod;
    private UUID salaryPeriodId;
    private StaffDto staff;
    private HrOrganizationDto organization;
    private UUID organizationId;
    private HRDepartmentDto department;
    private PositionTitleDto positionTitle;
    private UUID positionTitleId;
    
    private UUID departmentId; // dùng trong lấy dữ liệu cho CRM
    private String departmentCode; // dùng trong lấy dữ liệu cho CRM

    public List<UUID> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<UUID> staffIds) {
        this.staffIds = staffIds;
    }

    public Boolean getFutureDate() {
        return isFutureDate;
    }

    public void setFutureDate(Boolean futureDate) {
        isFutureDate = futureDate;
    }

    public Boolean getLeaveSchechual() {
        return isLeaveSchechual;
    }

    public void setLeaveSchechual(Boolean leaveSchechual) {
        isLeaveSchechual = leaveSchechual;
    }

    public UUID getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(UUID coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getShiftWorkId() {
        return shiftWorkId;
    }

    public void setShiftWorkId(UUID shiftWorkId) {
        this.shiftWorkId = shiftWorkId;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
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
    public UUID getPositionId() {
        return positionId;
    }

    @Override
    public void setPositionId(UUID positionId) {
        this.positionId = positionId;
    }

    @Override
    public Date getFromDate() {
        return fromDate;
    }

    @Override
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @Override
    public Date getToDate() {
        return toDate;
    }

    @Override
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Integer getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(Integer workingStatus) {
        this.workingStatus = workingStatus;
    }

    public Boolean getExportExcel() {
        return isExportExcel;
    }

    public void setExportExcel(Boolean exportExcel) {
        isExportExcel = exportExcel;
    }

    public UUID getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(UUID leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public Boolean getIsFutureDate() {
        return isFutureDate;
    }

    public void setIsFutureDate(Boolean isFutureDate) {
        this.isFutureDate = isFutureDate;
    }

    public Boolean getIsLeaveSchechual() {
        return isLeaveSchechual;
    }

    public void setIsLeaveSchechual(Boolean isLeaveSchechual) {
        this.isLeaveSchechual = isLeaveSchechual;
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
    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    @Override
    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

	public Boolean getIsExportExcel() {
		return isExportExcel;
	}

	public void setIsExportExcel(Boolean isExportExcel) {
		this.isExportExcel = isExportExcel;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public List<String> getStaffCodes() {
		return staffCodes;
	}

	public void setStaffCodes(List<String> staffCodes) {
		this.staffCodes = staffCodes;
	}
    
    
}
