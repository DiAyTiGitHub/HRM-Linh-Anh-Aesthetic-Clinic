package com.globits.hr.dto.search;

import com.globits.salary.dto.SalaryPeriodDto;

import java.util.Date;
import java.util.UUID;

// Tìm kiếm nhân viên theo hệ thống cấp bậc
public class SearchStaffHierarchyDto extends SearchDto {
    // Các trường lọc thông thường
    private UUID staffId;
    private UUID positionId;
    private UUID positionTitleId;
    private UUID departmentId;
    private UUID organizationId;
//    private Integer pageIndex;
//    private Integer pageSize;

    // Trường lọc liên quan đến cấp bậc
    private Integer levelNumber; // Cấp bậc cần lấy dữ liệu
    private Boolean collectInEachLevel; // Có lấy dữ liệu trên mỗi level (từ level của nhân viên hiện tại đến level cần tìm kiếm)


    @Override
    public UUID getStaffId() {
        return staffId;
    }

    @Override
    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
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
    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    @Override
    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
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
    public UUID getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public Boolean getCollectInEachLevel() {
        return collectInEachLevel;
    }

    public void setCollectInEachLevel(Boolean collectInEachLevel) {
        this.collectInEachLevel = collectInEachLevel;
    }
}
