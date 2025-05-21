package com.globits.timesheet.dto;

import com.globits.hr.dto.StaffDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimeSheetCalendarDto {
    private UUID staffId;
    private String staffName;
    private List<TimeSheetCalendarItemDto> items;

    public TimeSheetCalendarDto() {

    }

    public TimeSheetCalendarDto(List<TimeSheetCalendarItemDto> items, StaffDto entity) {
        this.items = new ArrayList<>();
        if (items != null && items.size() > 0) {
            this.items.addAll(items);
        }
        if (entity != null) {
            this.staffId = entity.getId();
            if (entity.getDisplayName() != null) {
                this.staffName = entity.getDisplayName();
            }
        }
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public List<TimeSheetCalendarItemDto> getItems() {
        return items;
    }

    public void setItems(List<TimeSheetCalendarItemDto> items) {
        this.items = items;
    }
}
