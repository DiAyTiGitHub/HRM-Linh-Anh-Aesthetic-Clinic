package com.globits.task.dto;

import com.globits.core.dto.BaseObjectDto;
import java.util.UUID;

public class HrSubTaskItemStaffDto extends BaseObjectDto {
    private UUID subTaskStaffId;

    private UUID staffId;

    public HrSubTaskItemStaffDto() {

    }

    public UUID getSubTaskStaffId() {
        return subTaskStaffId;
    }

    public void setSubTaskStaffId(UUID subTaskStaffId) {
        this.subTaskStaffId = subTaskStaffId;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

}
