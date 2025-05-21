package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Organization;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;

@Entity
@Table(name = "tbl_org_image")
public class OrganizationImage extends BaseObject {

    @OneToOne
    @JoinColumn(name = "organization_id", unique = true, nullable = false)
    private Organization organization;

    @Column(name = "image_path", nullable = true)
    private String imagePath;

    public Organization getOrganization() {
        return organization;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

