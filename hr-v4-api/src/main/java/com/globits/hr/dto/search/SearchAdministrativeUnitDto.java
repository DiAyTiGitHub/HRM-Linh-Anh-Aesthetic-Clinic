package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchAdministrativeUnitDto extends SearchDto {
    private UUID parentId;
    private Integer Level;
    private UUID provinceId;
    private UUID districtId;
    private UUID communeId;
    private Boolean includeParent = false;
    private Boolean exportExcel = false;

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return Level;
    }

    public void setLevel(Integer level) {
        Level = level;
    }

    public UUID getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(UUID provinceId) {
        this.provinceId = provinceId;
    }

    public UUID getDistrictId() {
        return districtId;
    }

    public void setDistrictId(UUID districtId) {
        this.districtId = districtId;
    }

    public UUID getCommuneId() {
        return communeId;
    }

    public void setCommuneId(UUID communeId) {
        this.communeId = communeId;
    }

    public Boolean getIncludeParent() {
        return includeParent;
    }

    public void setIncludeParent(Boolean includeParent) {
        this.includeParent = includeParent;
    }

    public Boolean getExportExcel() {
        return exportExcel;
    }

    public void setExportExcel(Boolean exportExcel) {
        this.exportExcel = exportExcel;
    }
}
