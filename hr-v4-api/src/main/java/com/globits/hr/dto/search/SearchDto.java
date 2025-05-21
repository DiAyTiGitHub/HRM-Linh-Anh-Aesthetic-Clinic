package com.globits.hr.dto.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SearchDto {
    protected UUID id;
    protected int pageIndex = 1;
    protected int pageSize = 10;
    protected String keyword;
    protected Boolean includeVoided = false;
    protected String orderBy;
    protected Integer academicTitleLevel;
    protected UUID academicTitleId;
    protected Integer educationDegreeLevel;
    protected String gender;
    protected UUID departmentId;
    protected List<UUID> departmentIdList = new ArrayList<>();
    protected UUID projectId;
    protected UUID projectActivityId;
    protected List<UUID> projectActivityIdList;
    protected List<UUID> projectIdList;
    protected List<UUID> staffIdList;
    protected Date fromDate;
    protected Date toDate;
    public boolean disablePaging;
    protected UUID taskId;
    protected UUID workingStatusId;
    protected UUID staffId;
    protected UUID allowanceId;
    protected UUID organizationId;
    protected UUID positionTitleId;
    protected Integer organizationType;
    protected UUID positionId;
    protected UUID modifierId;
    protected Integer yearReport;
    protected Integer monthYear;
    protected Integer priority;
    protected Boolean beingUsed;
    private Boolean voided;
    private Boolean hasSocialIns;
    private Boolean isManager;
    private Date date;
    private UUID removeId;
    private UUID salaryItemId;
    private UUID parentId;
    private UUID typeId;

    public UUID getTypeId() {
        return typeId;
    }

    public void setTypeId(UUID typeId) {
        this.typeId = typeId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public UUID getSalaryItemId() {
        return salaryItemId;
    }

    public void setSalaryItemId(UUID salaryItemId) {
        this.salaryItemId = salaryItemId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getYearReport() {
        return yearReport;
    }

    public void setYearReport(Integer yearReport) {
        this.yearReport = yearReport;
    }

    public Integer getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(Integer monthYear) {
        this.monthYear = monthYear;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getModifierId() {
        return modifierId;
    }

    public void setModifierId(UUID modifierId) {
        this.modifierId = modifierId;
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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getAcademicTitleLevel() {
        return academicTitleLevel;
    }

    public void setAcademicTitleLevel(Integer academicTitleLevel) {
        this.academicTitleLevel = academicTitleLevel;
    }

    public UUID getAcademicTitleId() {
        return academicTitleId;
    }

    public void setAcademicTitleId(UUID academicTitleId) {
        this.academicTitleId = academicTitleId;
    }

    public Integer getEducationDegreeLevel() {
        return educationDegreeLevel;
    }

    public void setEducationDegreeLevel(Integer educationDegreeLevel) {
        this.educationDegreeLevel = educationDegreeLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public boolean isDisablePaging() {
        return disablePaging;
    }

    public void setDisablePaging(boolean disablePaging) {
        this.disablePaging = disablePaging;
    }

    public UUID getProjectActivityId() {
        return projectActivityId;
    }

    public void setProjectActivityId(UUID projectActivityId) {
        this.projectActivityId = projectActivityId;
    }

    public List<UUID> getStaffIdList() {
        return staffIdList;
    }

    public void setStaffIdList(List<UUID> staffIdList) {
        this.staffIdList = staffIdList;
    }

    public List<UUID> getProjectActivityIdList() {
        return projectActivityIdList;
    }

    public void setProjectActivityIdList(List<UUID> projectActivityIdList) {
        this.projectActivityIdList = projectActivityIdList;
    }

    public List<UUID> getProjectIdList() {
        return projectIdList;
    }

    public void setProjectIdList(List<UUID> projectIdList) {
        this.projectIdList = projectIdList;
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

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getWorkingStatusId() {
        return workingStatusId;
    }

    public void setWorkingStatusId(UUID workingStatusId) {
        this.workingStatusId = workingStatusId;
    }

    public Boolean getBeingUsed() {
        return beingUsed;
    }

    public void setBeingUsed(Boolean beingUsed) {
        this.beingUsed = beingUsed;
    }

    public Boolean getIncludeVoided() {
        return includeVoided;
    }

    public void setIncludeVoided(Boolean includeVoided) {
        this.includeVoided = includeVoided;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public UUID getAllowanceId() {
        return allowanceId;
    }

    public void setAllowanceId(UUID allowanceId) {
        this.allowanceId = allowanceId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getPositionId() {
        return positionId;
    }

    public void setPositionId(UUID positionId) {
        this.positionId = positionId;
    }

    public Boolean getHasSocialIns() {
        return hasSocialIns;
    }

    public void setHasSocialIns(Boolean hasSocialIns) {
        this.hasSocialIns = hasSocialIns;
    }

    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public List<UUID> getDepartmentIdList() {
        return departmentIdList;
    }

    public void setDepartmentIdList(List<UUID> departmentIdList) {
        this.departmentIdList = departmentIdList;
    }

    public Boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(Boolean manager) {
        isManager = manager;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UUID getRemoveId() {
        return removeId;
    }

    public void setRemoveId(UUID removeId) {
        this.removeId = removeId;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
    }

    public Boolean getManager() {
        return isManager;
    }

    public void setManager(Boolean manager) {
        isManager = manager;
    }
}
