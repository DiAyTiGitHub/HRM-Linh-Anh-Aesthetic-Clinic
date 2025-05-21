package com.globits.hr.dto.diagram;

import java.util.UUID;

public class RequestGetChart {
    private UUID positionId;
    private UUID departmentId;
    private Integer numberOfLevel = 3;

    public RequestGetChart() {
    }

    public RequestGetChart(UUID positionId, Integer numberOfLevel) {
        this.positionId = positionId;
        this.numberOfLevel = numberOfLevel;
    }

    public UUID getPositionId() {
        return positionId;
    }

    public void setPositionId(UUID positionId) {
        this.positionId = positionId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getNumberOfLevel() {
        return numberOfLevel;
    }

    public void setNumberOfLevel(Integer numberOfLevel) {
        this.numberOfLevel = numberOfLevel;
    }
}
