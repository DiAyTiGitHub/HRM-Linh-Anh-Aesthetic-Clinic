package com.globits.timesheet.dto.calendar;

import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.*;

import java.util.Date;
import java.util.UUID;

public class ScheduledTimesheetDetailDto {
    private UUID id;
    private Date startTime;
    private Date endTime;
    private double duration;
    private String workingItemTitle;
    private TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto;
    private String note;
    private String addressIPCheckIn;
    private String addressIPCheckOut;

    public ScheduledTimesheetDetailDto() {

    }

    public ScheduledTimesheetDetailDto(TimeSheetDetail timeSheetDetail) {
        if (timeSheetDetail == null) return;

        this.setId(timeSheetDetail.getId());
        this.startTime = timeSheetDetail.getStartTime();
        this.endTime = timeSheetDetail.getEndTime();
        this.duration = timeSheetDetail.getDuration();
        this.workingItemTitle = timeSheetDetail.getWorkingItemTitle();
        this.note = timeSheetDetail.getNote();

        if (timeSheetDetail.getTimeSheetShiftWorkPeriod() != null) {
            this.timeSheetShiftWorkPeriodDto = new TimeSheetShiftWorkPeriodDto(timeSheetDetail.getTimeSheetShiftWorkPeriod());
        }

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getWorkingItemTitle() {
        return workingItemTitle;
    }

    public void setWorkingItemTitle(String workingItemTitle) {
        this.workingItemTitle = workingItemTitle;
    }

    public TimeSheetShiftWorkPeriodDto getTimeSheetShiftWorkPeriodDto() {
        return timeSheetShiftWorkPeriodDto;
    }

    public void setTimeSheetShiftWorkPeriodDto(TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto) {
        this.timeSheetShiftWorkPeriodDto = timeSheetShiftWorkPeriodDto;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAddressIPCheckIn() {
        return addressIPCheckIn;
    }

    public void setAddressIPCheckIn(String addressIPCheckIn) {
        this.addressIPCheckIn = addressIPCheckIn;
    }

    public String getAddressIPCheckOut() {
        return addressIPCheckOut;
    }

    public void setAddressIPCheckOut(String addressIPCheckOut) {
        this.addressIPCheckOut = addressIPCheckOut;
    }


}
