package com.globits.timesheet.dto;

import com.globits.hr.HrConstants;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.timesheet.domain.TimeSheet;
import com.globits.timesheet.domain.TimeSheetShiftWorkPeriod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimekeepingSummaryDto {
    private String staffName;
    private Date workingDate;
    private List<ShiftWorkTimePeriodDto> timePeriods = new ArrayList<>();

    public TimekeepingSummaryDto() {

    }

    public TimekeepingSummaryDto(TimeSheet timeSheet) {
        if (timeSheet.getStaff() != null) {
            this.staffName = timeSheet.getStaff().getDisplayName();
        }
        this.workingDate = DateTimeUtil.setTime(timeSheet.getWorkingDate(), 0, 0, 0, 0);
        if(!timeSheet.getTimeSheetShiftWorkPeriod().isEmpty()) {
            for (TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod : timeSheet.getTimeSheetShiftWorkPeriod()) {
                if(timeSheetShiftWorkPeriod.getWorkingFormat() != null && timeSheetShiftWorkPeriod.getWorkingFormat() != HrConstants.WorkingFormatEnum.off.getValue()) {
                    this.timePeriods.add(new ShiftWorkTimePeriodDto(timeSheetShiftWorkPeriod.getShiftWorkTimePeriod(), false));
                }
            }
        }
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public List<ShiftWorkTimePeriodDto> getTimePeriods() {
        return timePeriods;
    }

    public void setTimePeriods(List<ShiftWorkTimePeriodDto> timePeriods) {
        this.timePeriods = timePeriods;
    }
}
