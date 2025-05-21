package com.globits.timesheet.dto;

import java.util.*;

import com.globits.timesheet.domain.TimeSheet;
import com.globits.timesheet.domain.TimeSheetShiftWorkPeriod;

public class TimekeepingItemDto {
    private UUID timeSheetId;
    private List<TimeSheetShiftWorkPeriodDto> timeSheetShiftWorkPeriods;
    private UUID staffId;
    private String staffCode;
    private Date workingDate;
    private Integer year;
    private Integer month;
    private Integer day;
    private int pageIndex;
    private int pageSize;
    private String keyWord;

    public TimekeepingItemDto() {

    }

    public TimekeepingItemDto(TimeSheet timeSheet) {
        if (timeSheet != null) {
            this.setTimeSheetId(timeSheet.getId());
            this.workingDate = timeSheet.getWorkingDate();
            this.year = timeSheet.getYear();
            this.month = timeSheet.getMonth();
            this.day = timeSheet.getDay();

            if (timeSheet.getTimeSheetShiftWorkPeriod() != null) {
                this.timeSheetShiftWorkPeriods = new ArrayList<>();
                for (TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod : timeSheet.getTimeSheetShiftWorkPeriod()) {
                    this.timeSheetShiftWorkPeriods.add(new TimeSheetShiftWorkPeriodDto(timeSheetShiftWorkPeriod));
                }

                // Sort the list by startTime and endTime
                Collections.sort(this.timeSheetShiftWorkPeriods, new Comparator<TimeSheetShiftWorkPeriodDto>() {
                    @Override
                    public int compare(TimeSheetShiftWorkPeriodDto o1, TimeSheetShiftWorkPeriodDto o2) {
                        if (o1.getShiftWorkTimePeriod() == null || o2.getShiftWorkTimePeriod() == null) return 0;

                        Date startTime1 = o1.getShiftWorkTimePeriod().getStartTime();
                        Date startTime2 = o2.getShiftWorkTimePeriod().getStartTime();

                        // Compare start times
                        int result = (startTime1 != null && startTime2 != null)
                                ? startTime1.compareTo(startTime2)
                                : (startTime1 == null ? -1 : 1);

                        if (result != 0) {
                            return result;
                        }

                        // Compare end times if start times are equal
                        Date endTime1 = o1.getShiftWorkTimePeriod().getEndTime();
                        Date endTime2 = o2.getShiftWorkTimePeriod().getEndTime();

                        return (endTime1 != null && endTime2 != null)
                                ? endTime1.compareTo(endTime2)
                                : (endTime1 == null ? -1 : 1);
                    }
                });
            }
        }
    }

    public UUID getTimeSheetId() {
        return timeSheetId;
    }

    public void setTimeSheetId(UUID timeSheetId) {
        this.timeSheetId = timeSheetId;
    }

    public List<TimeSheetShiftWorkPeriodDto> getTimeSheetShiftWorkPeriods() {
        return timeSheetShiftWorkPeriods;
    }

    public void setTimeSheetShiftWorkPeriods(List<TimeSheetShiftWorkPeriodDto> timeSheetShiftWorkPeriods) {
        this.timeSheetShiftWorkPeriods = timeSheetShiftWorkPeriods;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
    
    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffCode() {
	return staffCode;
    }
    
    public void add(TimeSheetShiftWorkPeriodDto periodDto) {
	timeSheetShiftWorkPeriods.add(periodDto);
    }
}
