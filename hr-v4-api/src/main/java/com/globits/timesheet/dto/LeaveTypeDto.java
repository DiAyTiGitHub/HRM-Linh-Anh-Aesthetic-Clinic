package com.globits.timesheet.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.Label;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.domain.ProjectStaff;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LeaveTypeDto extends BaseObjectDto {
    private String name;
    private String code;
    private String description;
    private Boolean isPaid;
    private Boolean usedForRequest; // Có được sử dụng trong yêu cầu nghỉ phép

    public LeaveTypeDto() {
        super();
    }

    public LeaveTypeDto(String name, String code, String description, Boolean isPaid, Boolean usedForRequest) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.isPaid = isPaid;
        this.usedForRequest = usedForRequest;
    }

    public LeaveTypeDto(LeaveType entity) {
        super(entity);

        if (entity == null) return;

        this.code = entity.getCode();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.isPaid = entity.getIsPaid();
        this.usedForRequest = entity.getUsedForRequest();

    }

    public LeaveTypeDto(LeaveType entity, boolean isDetail) {
        this(entity);

        if (entity == null) return;

        if (!isDetail) return;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean paid) {
        isPaid = paid;
    }

    public Boolean getUsedForRequest() {
        return usedForRequest;
    }

    public void setUsedForRequest(Boolean usedForRequest) {
        this.usedForRequest = usedForRequest;
    }
}
