package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchOrganizationBranchDto extends SearchDto {
    protected UUID organizationId;

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }
}
