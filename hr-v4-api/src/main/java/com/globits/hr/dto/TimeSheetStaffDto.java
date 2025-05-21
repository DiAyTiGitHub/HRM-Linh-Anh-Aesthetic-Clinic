package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.TimeSheetStaff;
import com.globits.timesheet.dto.TimeSheetDto;

public class TimeSheetStaffDto extends BaseObjectDto {
    private StaffDto staff;
    private TimeSheetDto timeSheet;

    public TimeSheetStaffDto() {
        super();
    }

    public TimeSheetStaffDto(TimeSheetStaff entity) {
        if (entity != null) {
            if (entity.getStaff() != null) {
                this.staff = new StaffDto(entity.getStaff(), false);
            }
            if (entity.getTimesheet() != null) {
                this.timeSheet = new TimeSheetDto(entity.getTimesheet());
            }
        }
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public TimeSheetDto getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheetDto timeSheet) {
        this.timeSheet = timeSheet;
    }
}
