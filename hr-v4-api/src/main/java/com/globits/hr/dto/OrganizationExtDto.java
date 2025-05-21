package com.globits.hr.dto;

import com.globits.core.domain.Organization;
import com.globits.core.dto.OrganizationDto;

public class OrganizationExtDto extends OrganizationDto {
    private String imagePath;

    public OrganizationExtDto() {

    }

    public OrganizationExtDto(Organization org) {
        this.setCode(org.getCode());
        this.setWebsite(org.getWebsite());
        this.setOrganizationType(org.getOrganizationType());
        this.setId(org.getId());
        this.setName(org.getName());

        if (org.getParent() != null) this.setParent(new OrganizationDto(org.getParent()));
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
