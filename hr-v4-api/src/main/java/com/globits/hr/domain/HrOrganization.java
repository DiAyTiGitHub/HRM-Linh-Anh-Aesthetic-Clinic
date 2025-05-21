package com.globits.hr.domain;

import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.Organization;
import com.globits.core.domain.OrganizationUser;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("HrOrganization")
public class HrOrganization extends Organization {
    @OneToMany(
            mappedBy = "organization",
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private Set<HRDepartment> departments;//danh sách phòng ban, chi nhánh trực thuộc

    @Column(name = "sort_number")
    private Integer sortNumber;

    @ManyToOne
    @JoinColumn(name = "administrativeUnit_id")
    private AdministrativeUnit administrativeUnit; //địa chỉ của đơn vị

    @Column(name = "address_detail")
    private String addressDetail; // Chi tiết đơn vị/pháp nhân

    @Column(name = "tax_code")
    private String taxCode; // Mã số thuế

    @Column(name = "founded_date")
    private Date foundedDate; // Ngày thành lập

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representative_id")
    private Staff representative; // Nhân viên đại diện


    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public Date getFoundedDate() {
        return foundedDate;
    }

    public void setFoundedDate(Date foundedDate) {
        this.foundedDate = foundedDate;
    }

    public Staff getRepresentative() {
        return representative;
    }

    public void setRepresentative(Staff representative) {
        this.representative = representative;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Set<HRDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<HRDepartment> departments) {
        this.departments = departments;
    }

    public AdministrativeUnit getAdministrativeUnit() {
        return administrativeUnit;
    }

    public void setAdministrativeUnit(AdministrativeUnit administrativeUnit) {
        this.administrativeUnit = administrativeUnit;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }
}
