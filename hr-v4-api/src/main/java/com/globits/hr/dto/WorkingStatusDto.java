package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.WorkingStatus;

public class WorkingStatusDto extends BaseObjectDto {
    private String code;
    private String name;
    private Double statusValue;
    private Long totalOfTasksInStatus;

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

    public Double getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(Double statusValue) {
        this.statusValue = statusValue;
    }

    public Long getTotalOfTasksInStatus() {
        return totalOfTasksInStatus;
    }

    public void setTotalOfTasksInStatus(Long totalOfTasksInStatus) {
        this.totalOfTasksInStatus = totalOfTasksInStatus;
    }

    public WorkingStatusDto() {

    }

    public WorkingStatusDto(WorkingStatus entity) {
        this.id = entity.getId();
        this.code = entity.getCode();
        this.name = entity.getName();
        this.statusValue = entity.getStatusValue();
    }

}
