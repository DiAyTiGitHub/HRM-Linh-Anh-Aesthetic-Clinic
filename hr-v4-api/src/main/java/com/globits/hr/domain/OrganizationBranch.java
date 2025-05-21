package com.globits.hr.domain;

import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Country;
import com.globits.core.domain.Organization;
import com.globits.hr.rest.RestHrAdministrativeUnitController;
import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "tbl_organization_branch")
public class OrganizationBranch extends BaseObject {

    private static final long serialVersionUID = 1L;

    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "province_id")
    private AdministrativeUnit province;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id")
    private AdministrativeUnit district;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commune_id")
    private AdministrativeUnit commune;

    private String address;

    private String phoneNumber;

    @Column(name = "note")
    private String note;


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

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public AdministrativeUnit getProvince() {
        return province;
    }

    public void setProvince(AdministrativeUnit province) {
        this.province = province;
    }

    public AdministrativeUnit getDistrict() {
        return district;
    }

    public void setDistrict(AdministrativeUnit district) {
        this.district = district;
    }

    public AdministrativeUnit getCommune() {
        return commune;
    }

    public void setCommune(AdministrativeUnit commune) {
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
