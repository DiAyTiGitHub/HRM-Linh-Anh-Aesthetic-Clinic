package com.globits.hr.dto.importExcel;

import com.globits.hr.domain.HrOrganization;
import com.globits.hr.domain.PersonBankAccount;
import com.globits.hr.domain.Staff;

import java.util.Date;

public class HrOrganizationImport {
    private String code; // 1. Mã đơn vị
    private String name; // 2. Tên đơn vị
    private String website; // 3. Website
    private String parentOrgCode; // 4. Mã đơn vị trực thuộc
    private String parentOrgName; // 5. Đơn vị trực thuộc
    private String taxCode; //  6. Mã số thuế
    private Date foundDate; // 7. Ngày thành lập
    private String representativeCode; // 8. Mã nhân viên đại diện
    private String representativeName; // 9. Nhân viên đại diện
    private String provinceCode;
    private String provinceName;
    private String districtCode;
    private String districtName;
    private String communeCode;
    private String communeName;
    private String addressDetail;
    private Integer organizationType;
    private String errorMessage;


    public HrOrganizationImport() {
    }

    public HrOrganizationImport(HrOrganization entity) {
        if (entity == null) return;

        this.code = entity.getCode();
        this.name = entity.getName();
        this.website = entity.getWebsite();
        this.taxCode = entity.getTaxCode();
        this.foundDate = entity.getFoundedDate();

        if (entity.getParent() != null) {
            this.parentOrgCode = entity.getParent().getCode();
            this.parentOrgName = entity.getParent().getName();
        }

        if (entity.getRepresentative() != null) {
            this.representativeCode = entity.getRepresentative().getStaffCode();
            this.representativeName = entity.getRepresentative().getDisplayName();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getParentOrgCode() {
        return parentOrgCode;
    }

    public void setParentOrgCode(String parentOrgCode) {
        this.parentOrgCode = parentOrgCode;
    }

    public String getParentOrgName() {
        return parentOrgName;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public Date getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(Date foundDate) {
        this.foundDate = foundDate;
    }

    public String getRepresentativeCode() {
        return representativeCode;
    }

    public void setRepresentativeCode(String representativeCode) {
        this.representativeCode = representativeCode;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public void setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getCommuneCode() {
        return communeCode;
    }

    public void setCommuneCode(String communeCode) {
        this.communeCode = communeCode;
    }

    public String getCommuneName() {
        return communeName;
    }

    public void setCommuneName(String communeName) {
        this.communeName = communeName;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
    }
}
