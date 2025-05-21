package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchPositionRole extends SearchDto {
    protected UUID positionRoleId;

    public UUID getPositionRoleId() {
        return positionRoleId;
    }

    public void setPositionRoleId(UUID positionRoleId) {
        this.positionRoleId = positionRoleId;
    }
}
