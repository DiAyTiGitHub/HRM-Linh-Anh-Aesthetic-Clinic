package com.globits.hr.dto.search;

import java.util.UUID;

public class PositionTitleSearchDto extends SearchDto {
    private UUID rankTitleId;
    private UUID parentId;
    private UUID positionRoleId;
    private String departmentCode;
    private UUID departmentId;
    private Boolean isExportExcel;
    private Integer type;
    private Boolean isGroup;

    public UUID getRankTitleId() {
        return rankTitleId;
    }

    public void setRankTitleId(UUID rankTitleId) {
        this.rankTitleId = rankTitleId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public UUID getPositionRoleId() {
        return positionRoleId;
    }

    public void setPositionRoleId(UUID positionRoleId) {
        this.positionRoleId = positionRoleId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    @Override
    public UUID getDepartmentId() {
        return departmentId;
    }

    @Override
    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public Boolean getIsExportExcel() {
        return isExportExcel;
    }

    public void setIsExportExcel(Boolean exportExcel) {
        isExportExcel = exportExcel;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }
}
