package com.globits.hr.dto.search;

import java.util.UUID;

public class PositionStaffSearchDto extends SearchDto {
    private UUID supervisorId;
    private Integer relationshipType;

    public UUID getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(UUID supervisorId) {
        this.supervisorId = supervisorId;
    }

    public Integer getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(Integer relationshipType) {
        this.relationshipType = relationshipType;
    }
}
