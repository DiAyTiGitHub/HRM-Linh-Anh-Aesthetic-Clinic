package com.globits.timesheet.dto;

public class TimeSheetImportItemDto {
    private String staffCode;
    private Integer day;
    private Integer month;
    private Integer year;
    private Integer morningShift;
    private Integer afternoonShift;
    private Integer eveningShift;

    public TimeSheetImportItemDto() {

    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMorningShift() {
        return morningShift;
    }

    public void setMorningShift(Integer morningShift) {
        this.morningShift = morningShift;
    }

    public Integer getAfternoonShift() {
        return afternoonShift;
    }

    public void setAfternoonShift(Integer afternoonShift) {
        this.afternoonShift = afternoonShift;
    }

    public Integer getEveningShift() {
        return eveningShift;
    }

    public void setEveningShift(Integer eveningShift) {
        this.eveningShift = eveningShift;
    }
}
