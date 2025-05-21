package com.globits.timesheet.dto.search;

import java.util.Date;
import java.util.UUID;

public class SearchJournalDto {
    private UUID id;
    private int pageIndex;
    private int pageSize;
    private String name;
    private String description;
    private Date journalDate;
    private Date fromDate;
    private Date toDate;
    private String location;
    private Integer type;

    private UUID StaffId;
    private Integer monthReport;

    private Integer yearReport;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getJournalDate() {
        return journalDate;
    }

    public void setJournalDate(Date journalDate) {
        this.journalDate = journalDate;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public UUID getStaffId() {
        return StaffId;
    }

    public void setStaffId(UUID staffId) {
        StaffId = staffId;
    }
}
