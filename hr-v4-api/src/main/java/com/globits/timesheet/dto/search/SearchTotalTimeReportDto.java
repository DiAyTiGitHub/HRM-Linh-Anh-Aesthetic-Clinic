package com.globits.timesheet.dto.search;

public class SearchTotalTimeReportDto extends SearchTimeSheetDto{
    private Integer timeReport; // =1: report tuần, =2: report Tháng, =3: report theo năm
    private Integer weekReport;
    private Integer monthReport;
    private Integer yearReport;

    public Integer getTimeReport() {
        return timeReport;
    }

    public void setTimeReport(Integer timeReport) {
        this.timeReport = timeReport;
    }

    public Integer getWeekReport() {
        return weekReport;
    }

    public void setWeekReport(Integer weekReport) {
        this.weekReport = weekReport;
    }

    public Integer getMonthReport() {
        return monthReport;
    }

    public void setMonthReport(Integer monthReport) {
        this.monthReport = monthReport;
    }

    public Integer getYearReport() {
        return yearReport;
    }

    public void setYearReport(Integer yearReport) {
        this.yearReport = yearReport;
    }
}
