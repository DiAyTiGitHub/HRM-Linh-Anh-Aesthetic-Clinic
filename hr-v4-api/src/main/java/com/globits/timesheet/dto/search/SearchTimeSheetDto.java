package com.globits.timesheet.dto.search;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.dto.SalaryPeriodDto;

import java.util.Date;
import java.util.UUID;

public class SearchTimeSheetDto {
    private UUID id;
    private int pageIndex;
    private int pageSize;
    private String keyword;
    private Date workingDate;
    private String staffCode;
    private String addressIPCheckIn;
    private String addressIPCheckOut;


    private String displayName;
    private String codeAndName;
    private UUID projectId;
    private UUID staffId;
    private UUID shiftWorkId;
    private Date fromDate;
    private Date toDate;
    private UUID workingStatusId;

    private Integer priority;
    private boolean isExportExcel;
    private UUID projectActivityId;

    private Integer timeReport; // =1: report tuần, =2: report Tháng, =3: report theo năm
    private Integer weekReport;
    private Integer monthReport;
    private Integer yearReport;
    private Integer dayReport;

    private UUID organizationId;
    private UUID departmentId;
    private UUID positionTitleId;
    private UUID positionId;

    private SalaryPeriodDto salaryPeriod;
    private UUID salaryPeriodId;
    private StaffDto staff;
    private HrOrganizationDto organization;
    private HRDepartmentDto department;
    private PositionTitleDto positionTitle;
    private Boolean notScheduled;

    public Integer getDayReport() {
        return dayReport;
    }

    public void setDayReport(Integer dayReport) {
        this.dayReport = dayReport;
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

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCodeAndName() {
        return codeAndName;
    }

    public void setCodeAndName(String codeAndName) {
        this.codeAndName = codeAndName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
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

    public UUID getWorkingStatusId() {
        return workingStatusId;
    }

    public void setWorkingStatusId(UUID workingStatusId) {
        this.workingStatusId = workingStatusId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public boolean getIsExportExcel() {
        return isExportExcel;
    }

    public void setExportExcel(boolean isExportExcel) {
        this.isExportExcel = isExportExcel;
    }

    public UUID getProjectActivityId() {
        return projectActivityId;
    }

    public void setProjectActivityId(UUID projectActivityId) {
        this.projectActivityId = projectActivityId;
    }

    public boolean isExportExcel() {
        return isExportExcel;
    }

    public Integer getTimeReport() {
        return timeReport;
    }

    public void setTimeReport(Integer timeReport) {
        this.timeReport = timeReport;
    }

    public Integer getWeekReport() {
        return weekReport;
    }

    public void setWeekReport(Integer weekReport) {
        this.weekReport = weekReport;
    }

    public Integer getMonthReport() {
        return monthReport;
    }

    public void setMonthReport(Integer monthReport) {
        this.monthReport = monthReport;
    }

    public Integer getYearReport() {
        return yearReport;
    }

    public void setYearReport(Integer yearReport) {
        this.yearReport = yearReport;
    }

    public String getAddressIPCheckIn() {
        return addressIPCheckIn;
    }

    public void setAddressIPCheckIn(String addressIPCheckIn) {
        this.addressIPCheckIn = addressIPCheckIn;
    }

    public String getAddressIPCheckOut() {
        return addressIPCheckOut;
    }

    public void setAddressIPCheckOut(String addressIPCheckOut) {
        this.addressIPCheckOut = addressIPCheckOut;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public UUID getPositionId() {
        return positionId;
    }

    public void setPositionId(UUID positionId) {
        this.positionId = positionId;
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

    public Boolean getNotScheduled() {
        return notScheduled;
    }

    public void setNotScheduled(Boolean notScheduled) {
        this.notScheduled = notScheduled;
    }
}
