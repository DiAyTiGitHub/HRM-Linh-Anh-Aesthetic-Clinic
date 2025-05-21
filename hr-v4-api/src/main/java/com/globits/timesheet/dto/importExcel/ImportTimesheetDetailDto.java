package com.globits.timesheet.dto.importExcel;

public class ImportTimesheetDetailDto {
    private String stt;
    private String staffCode;
    private String staffName;
    private String workingDate;
    private String shiftWorkCode;
    private String shiftWorkName;
    private String shiftWorkPeriodCode;

    private String checkInTime;
    private String checkInIP;

    private String checkOutTime;
    private String checkOutIP;

    public ImportTimesheetDetailDto() {
    }


    public String getStt() {
        return stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(String workingDate) {
        this.workingDate = workingDate;
    }

    public String getShiftWorkCode() {
        return shiftWorkCode;
    }

    public void setShiftWorkCode(String shiftWorkCode) {
        this.shiftWorkCode = shiftWorkCode;
    }

    public String getShiftWorkName() {
        return shiftWorkName;
    }

    public void setShiftWorkName(String shiftWorkName) {
        this.shiftWorkName = shiftWorkName;
    }

    public String getShiftWorkPeriodCode() {
        return shiftWorkPeriodCode;
    }

    public void setShiftWorkPeriodCode(String shiftWorkPeriodCode) {
        this.shiftWorkPeriodCode = shiftWorkPeriodCode;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckInIP() {
        return checkInIP;
    }

    public void setCheckInIP(String checkInIP) {
        this.checkInIP = checkInIP;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getCheckOutIP() {
        return checkOutIP;
    }

    public void setCheckOutIP(String checkOutIP) {
        this.checkOutIP = checkOutIP;
    }
}
