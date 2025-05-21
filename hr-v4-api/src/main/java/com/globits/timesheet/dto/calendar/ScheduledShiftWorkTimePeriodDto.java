package com.globits.timesheet.dto.calendar;

import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.timesheet.dto.TimeSheetDetailDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ScheduledShiftWorkTimePeriodDto {
    private UUID id;
    private Date endTime;
    private Date startTime;
    private String code;
    private String displayTime;

    public ScheduledShiftWorkTimePeriodDto() {

    }

    public ScheduledShiftWorkTimePeriodDto(ShiftWorkTimePeriod timePeriod) {
        if (timePeriod == null) return;

        this.id = timePeriod.getId();
        this.code = timePeriod.getCode();
        this.endTime = timePeriod.getEndTime();
        this.startTime = timePeriod.getStartTime();

        this.displayTime = DateTimeUtil.getHourMinutes(timePeriod.getStartTime()) + " - " + DateTimeUtil.getHourMinutes(timePeriod.getEndTime());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }
}

