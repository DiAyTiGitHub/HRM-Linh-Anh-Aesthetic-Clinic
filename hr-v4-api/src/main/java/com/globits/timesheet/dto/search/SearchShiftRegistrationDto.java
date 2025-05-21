package com.globits.timesheet.dto.search;

import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.StaffDto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SearchShiftRegistrationDto {
    private UUID id;
    private int pageIndex;
    private int pageSize;
    private String keyword;
    private Date fromDate;
    private Date toDate;
    private StaffDto registerStaff; // nhân viên đăng ký
    private ShiftWorkDto shiftWork; // ca làm việc đăng ký
    private LocalDateTime workingDate; // ngày làm việc
    private StaffDto approvalStaff; // nhân viên phê duyệt
    private Integer status; //HrConstants.ShiftRegistrationApprovalStatus
    private List<UUID> chosenIds;
    private UUID approvalStaffId;
    private UUID organizationId;
    private UUID departmentId;
    private UUID positionTitleId;

    public SearchShiftRegistrationDto() {

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

    public StaffDto getRegisterStaff() {
        return registerStaff;
    }

    public void setRegisterStaff(StaffDto registerStaff) {
        this.registerStaff = registerStaff;
    }

    public ShiftWorkDto getShiftWork() {
        return shiftWork;
    }

    public void setShiftWork(ShiftWorkDto shiftWork) {
        this.shiftWork = shiftWork;
    }

    public LocalDateTime getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(LocalDateTime workingDate) {
        this.workingDate = workingDate;
    }

    public StaffDto getApprovalStaff() {
        return approvalStaff;
    }

    public void setApprovalStaff(StaffDto approvalStaff) {
        this.approvalStaff = approvalStaff;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<UUID> getChosenIds() {
        return chosenIds;
    }

    public void setChosenIds(List<UUID> chosenIds) {
        this.chosenIds = chosenIds;
    }

    public UUID getApprovalStaffId() {
        return approvalStaffId;
    }

    public void setApprovalStaffId(UUID approvalStaffId) {
        this.approvalStaffId = approvalStaffId;
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
}
