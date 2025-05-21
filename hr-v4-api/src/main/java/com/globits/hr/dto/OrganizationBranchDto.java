package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.CountryDto;
import com.globits.core.dto.OrganizationDto;
import com.globits.hr.domain.OrganizationBranch;
import org.checkerframework.checker.units.qual.C;

import java.util.UUID;

public class OrganizationBranchDto extends BaseObjectDto {
    private String code;
    private String name;
    private OrganizationDto organization;
    private CountryDto country;
    private HrAdministrativeUnitDto province;
    private HrAdministrativeUnitDto district;
    private HrAdministrativeUnitDto commune;
    private String address;
    private String phoneNumber;
    private String note;

    public OrganizationBranchDto() {
    }

    public OrganizationBranchDto(OrganizationBranch entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.address = entity.getAddress();
            this.phoneNumber = entity.getPhoneNumber();
            this.note = entity.getNote();

            if (entity.getOrganization() != null) {
                this.organization = new OrganizationDto(entity.getOrganization());
            }

            if (entity.getCountry() != null) {
                this.country = new CountryDto(entity.getCountry());
            }

            if (entity.getDistrict() != null) {
                this.district = new HrAdministrativeUnitDto(entity.getDistrict());
            }

            if (entity.getProvince() != null) {
                this.province = new HrAdministrativeUnitDto(entity.getProvince());
            }

            if (entity.getCommune() != null) {
                this.commune = new HrAdministrativeUnitDto(entity.getCommune());
            }
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

    public OrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDto organization) {
        this.organization = organization;
    }

    public CountryDto getCountry() {
        return country;
    }

    public void setCountry(CountryDto country) {
        this.country = country;
    }

    public HrAdministrativeUnitDto getProvince() {
        return province;
    }

    public void setProvince(HrAdministrativeUnitDto province) {
        this.province = province;
    }

    public HrAdministrativeUnitDto getDistrict() {
        return district;
    }

    public void setDistrict(HrAdministrativeUnitDto district) {
        this.district = district;
    }

    public HrAdministrativeUnitDto getCommune() {
        return commune;
    }

    public void setCommune(HrAdministrativeUnitDto commune) {
        this.commune = commune;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
