package com.globits.hr.dto;

import java.util.Date;
import java.util.List;

import com.globits.hr.domain.WorkingStatus;

public class StaffShiftWorkDto {
    private List<StaffDto> staffs;
    private ShiftWorkDto shiftWork;
    private Date fromDate;
    private Date toDate;
    private WorkingStatus workingStatus;
    private Integer workingOnSunday; // 1 lam ca Chu nhat, 0 bo ngay chu nhat

    public List<StaffDto> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<StaffDto> staffs) {
        this.staffs = staffs;
    }

    public ShiftWorkDto getShiftWork() {
        return shiftWork;
    }

    public void setShiftWork(ShiftWorkDto shiftWork) {
        this.shiftWork = shiftWork;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public WorkingStatus getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(WorkingStatus workingStatus) {
        this.workingStatus = workingStatus;
    }

    public Integer getWorkingOnSunday() {
        return workingOnSunday;
    }

    public void setWorkingOnSunday(Integer workingOnSunday) {
        this.workingOnSunday = workingOnSunday;
    }
}
