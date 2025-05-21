package com.globits.timesheet.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.timesheet.domain.TimeSheetShiftWorkPeriod;

public class TimeSheetShiftWorkPeriodDto extends BaseObjectDto {
    private ShiftWorkTimePeriodDto shiftWorkTimePeriod;
    private String code;
    private TimeSheetDto timeSheet;
    private String note;
    private Integer workingFormat;

    public TimeSheetShiftWorkPeriodDto() {
    }

    public TimeSheetShiftWorkPeriodDto(TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod) {
        if (timeSheetShiftWorkPeriod != null) {
            setId(timeSheetShiftWorkPeriod.getId());
            this.note = timeSheetShiftWorkPeriod.getNote();
            this.workingFormat = timeSheetShiftWorkPeriod.getWorkingFormat();
            if (timeSheetShiftWorkPeriod.getTimeSheet() != null) {
                this.timeSheet = new TimeSheetDto(timeSheetShiftWorkPeriod.getTimeSheet(), false);
            }
            if (timeSheetShiftWorkPeriod.getShiftWorkTimePeriod() != null) {
                this.shiftWorkTimePeriod = new ShiftWorkTimePeriodDto(timeSheetShiftWorkPeriod.getShiftWorkTimePeriod());
            }
        }
    }

    public TimeSheetShiftWorkPeriodDto(TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod, boolean collapse) {
        if (timeSheetShiftWorkPeriod != null) {
            setId(timeSheetShiftWorkPeriod.getId());
            this.note = timeSheetShiftWorkPeriod.getNote();
            this.workingFormat = timeSheetShiftWorkPeriod.getWorkingFormat();
            if (!collapse) {
                if (timeSheetShiftWorkPeriod.getTimeSheet() != null) {
                    this.timeSheet = new TimeSheetDto();
                    this.timeSheet.setId(timeSheetShiftWorkPeriod.getTimeSheet().getId());
                    this.timeSheet.setWorkingDate(timeSheetShiftWorkPeriod.getTimeSheet().getWorkingDate());
                }
                if (timeSheetShiftWorkPeriod.getShiftWorkTimePeriod() != null) {
                    this.shiftWorkTimePeriod = new ShiftWorkTimePeriodDto();
                    this.shiftWorkTimePeriod.setId(timeSheetShiftWorkPeriod.getShiftWorkTimePeriod().getId());
                    this.shiftWorkTimePeriod.setEndTime(timeSheetShiftWorkPeriod.getShiftWorkTimePeriod().getEndTime());
                    this.shiftWorkTimePeriod.setStartTime(timeSheetShiftWorkPeriod.getShiftWorkTimePeriod().getStartTime());
                }
            }
        }
    }
    
    public void setCode(String code) {
	this.code = code;
    }
    
    public String getCode() {
	return code;
    }

    public ShiftWorkTimePeriodDto getShiftWorkTimePeriod() {
        return shiftWorkTimePeriod;
    }

    public void setShiftWorkTimePeriod(ShiftWorkTimePeriodDto shiftWorkTimePeriod) {
        this.shiftWorkTimePeriod = shiftWorkTimePeriod;
    }

    public TimeSheetDto getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheetDto timeSheet) {
        this.timeSheet = timeSheet;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getWorkingFormat() {
        return workingFormat;
    }

    public void setWorkingFormat(Integer workingFormat) {
        this.workingFormat = workingFormat;
    }
}
