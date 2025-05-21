package com.globits.task.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.task.domain.HrSubTaskItem;
import com.globits.task.domain.HrSubTaskItemStaff;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HrSubTaskItemDto extends BaseObjectDto {
    private UUID subTaskId;
    private String name;
    private String code;
    private String description;
    private List<StaffDto> staffs;
    private Date startTime;
    private Date endTime;
    private boolean value;

    public HrSubTaskItemDto() {

    }

    public HrSubTaskItemDto(HrSubTaskItem entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
            this.startTime = entity.getStartTime();
            this.endTime = entity.getEndTime();
            this.value = entity.isValue();
            if (entity.getSubTask() != null) {
                this.subTaskId = entity.getSubTask().getId();
            }
            if (entity.getStaffs() != null) {
                this.staffs = new ArrayList<>();
                for (HrSubTaskItemStaff taskStaff : entity.getStaffs()) {
                    StaffDto staff = new StaffDto();
                    if (taskStaff.getStaff() != null) {
                        staff.setId(taskStaff.getStaff().getId());
                        staff.setDisplayName(taskStaff.getStaff().getDisplayName());
                        this.staffs.add(staff);
                    }

                }
            }
        }
    }

    public UUID getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(UUID subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<StaffDto> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<StaffDto> staffs) {
        this.staffs = staffs;
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

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
