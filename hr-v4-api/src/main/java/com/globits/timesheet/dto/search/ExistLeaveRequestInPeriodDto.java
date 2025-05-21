package com.globits.timesheet.dto.search;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ExistLeaveRequestInPeriodDto {
    private List<UUID> staffIds;
    private Date fromDate;
    private Date toDate;

    public ExistLeaveRequestInPeriodDto() {
    }

    public List<UUID> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<UUID> staffIds) {
        this.staffIds = staffIds;
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
}
