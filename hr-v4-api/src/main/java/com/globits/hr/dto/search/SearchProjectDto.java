package com.globits.hr.dto.search;

import java.util.Date;

public class SearchProjectDto extends SearchDto {
    private Boolean isFinished = false;
    private Date startDate;
    private Date endDate;

    public SearchProjectDto() {
    }

    public Boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(Boolean isFinished) {
        this.isFinished = isFinished;
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
}
