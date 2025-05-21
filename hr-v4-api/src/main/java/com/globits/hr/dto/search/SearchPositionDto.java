package com.globits.hr.dto.search;

import java.util.List;
import java.util.UUID;

public class SearchPositionDto extends SearchDto {
    private UUID staffId;
    private UUID positionTitleId; // Chức danh
    private UUID rankTitleId; // Cấp bậc
    private UUID organizationId;
    private UUID departmentId;
    private String departmentCode;
    private List<UUID> chosenIds;
    private boolean getOwn = false;
    private Boolean isVacant;
    private Boolean exportExcel;
    private Boolean isPublic;
    private Boolean isOldPosition;

    public Boolean getExportExcel() {
        return exportExcel;
    }

    public void setExportExcel(Boolean exportExcel) {
        this.exportExcel = exportExcel;
    }

    public Boolean getVacant() {
        return isVacant;
    }

    public void setVacant(Boolean vacant) {
        isVacant = vacant;
    }

    @Override
    public UUID getStaffId() {
        return staffId;
    }

    @Override
    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public UUID getRankTitleId() {
        return rankTitleId;
    }

    public void setRankTitleId(UUID rankTitleId) {
        this.rankTitleId = rankTitleId;
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

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getOldPosition() {
        return isOldPosition;
    }

    public void setOldPosition(Boolean oldPosition) {
        isOldPosition = oldPosition;
    }

    public List<UUID> getChosenIds() {
        return chosenIds;
    }

    public void setChosenIds(List<UUID> chosenIds) {
        this.chosenIds = chosenIds;
    }

    public boolean isGetOwn() {
        return getOwn;
    }

    public void setGetOwn(boolean getOwn) {
        this.getOwn = getOwn;
    }

    public Boolean getIsVacant() {
        return isVacant;
    }

    public void setIsVacant(Boolean isVacant) {
        this.isVacant = isVacant;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
}
