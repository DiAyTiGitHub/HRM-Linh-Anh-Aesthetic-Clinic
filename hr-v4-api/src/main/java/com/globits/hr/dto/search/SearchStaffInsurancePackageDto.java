package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchStaffInsurancePackageDto extends SearchDto {
    private UUID insurancePackageId;


    public UUID getInsurancePackageId() {
        return insurancePackageId;
    }

    public void setInsurancePackageId(UUID insurancePackageId) {
        this.insurancePackageId = insurancePackageId;
    }
}
