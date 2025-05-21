package com.globits.hr.dto;

import java.util.Date;

public class SynthesisReportOfStaffDto {
    private StaffDto staff;    //nhân viên
    private Date startDate;
    private Date endDate;
    private double totalNumberOfWorkingDays;    //tổng số ngày làm việc
    private Integer totalHolidays;    //tổng số ngày nghỉ
    private Integer totalNumberOfWorkingDaysInHalf;  //tổng số ngày làm việc một nửa
    private String salaryPercentage;    //tỷ lệ lương trong tháng

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getTotalNumberOfWorkingDays() {
        return totalNumberOfWorkingDays;
    }

    public void setTotalNumberOfWorkingDays(double totalNumberOfWorkingDays) {
        this.totalNumberOfWorkingDays = totalNumberOfWorkingDays;
    }

    public Integer getTotalHolidays() {
        return totalHolidays;
    }

    public void setTotalHolidays(Integer totalHolidays) {
        this.totalHolidays = totalHolidays;
    }

    public Integer getTotalNumberOfWorkingDaysInHalf() {
        return totalNumberOfWorkingDaysInHalf;
    }

    public void setTotalNumberOfWorkingDaysInHalf(Integer totalNumberOfWorkingDaysInHalf) {
        this.totalNumberOfWorkingDaysInHalf = totalNumberOfWorkingDaysInHalf;
    }

    public String getSalaryPercentage() {
        return salaryPercentage;
    }

    public void setSalaryPercentage(String salaryPercentage) {
        this.salaryPercentage = salaryPercentage;
    }

}
