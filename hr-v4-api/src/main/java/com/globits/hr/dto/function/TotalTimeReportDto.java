package com.globits.hr.dto.function;

import java.util.UUID;

public class TotalTimeReportDto {
    private String staffName;
    private UUID staffId;
    private UUID timeSheetId;
    private String civilServant;
    private String department;
    private double totalTime;
    private Integer week;
    private Integer month;
    private Integer year;

    public TotalTimeReportDto(UUID staffId, String staffName, Double totalTime, Integer week, Integer month, Integer year) {
        super();
        this.staffId = staffId;
        this.staffName = staffName;
        if (totalTime != null) {
            this.totalTime = totalTime;
        }
        this.week = week;
        this.month = month;
        this.year = year;
    }

    public TotalTimeReportDto(UUID staffId, String staffName, UUID timeSheetId, Double totalTime, Integer week, Integer month, Integer year) {
        super();
        this.staffId = staffId;
        this.staffName = staffName;
        this.timeSheetId = timeSheetId;
        if (totalTime != null) {
            this.totalTime = totalTime;
        }
        this.week = week;
        this.month = month;
        this.year = year;
    }
    public TotalTimeReportDto(){

    }


    public UUID getTimeSheetId() {
        return timeSheetId;
    }

    public void setTimeSheetId(UUID timeSheetId) {
        this.timeSheetId = timeSheetId;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
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

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
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

    public String getCivilServant() {
        return civilServant;
    }

    public void setCivilServant(String civilServant) {
        this.civilServant = civilServant;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
