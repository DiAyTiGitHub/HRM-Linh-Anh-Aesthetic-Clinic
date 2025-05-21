package com.globits.hr.dto.search;

import java.util.Date;

public class SearchTaskDto extends SearchDto {
    private Boolean tasksOfAllProjects;
    private Boolean increasingCodeOrder;
    private Boolean increasingPriorityOrder;
    private Boolean increasingLastModifyDate;
    private Boolean includeChildrenActivities;

    private Date fromDateUpdate;

    private Date toDateUpdate;

    public SearchTaskDto() {
    }

    public Boolean getTasksOfAllProjects() {
        return tasksOfAllProjects;
    }

    public void setTasksOfAllProjects(Boolean tasksOfAllProjects) {
        this.tasksOfAllProjects = tasksOfAllProjects;
    }

    public Boolean getIncreasingCodeOrder() {
        return increasingCodeOrder;
    }

    public void setIncreasingCodeOrder(Boolean increasingCodeOrder) {
        this.increasingCodeOrder = increasingCodeOrder;
    }

    public Boolean getIncreasingPriorityOrder() {
        return increasingPriorityOrder;
    }

    public void setIncreasingPriorityOrder(Boolean increasingPriorityOrder) {
        this.increasingPriorityOrder = increasingPriorityOrder;
    }

    public Boolean getIncreasingLastModifyDate() {
        return increasingLastModifyDate;
    }

    public void setIncreasingLastModifyDate(Boolean increasingLastModifyDate) {
        this.increasingLastModifyDate = increasingLastModifyDate;
    }

    public Boolean getIncludeChildrenActivities() {
        return includeChildrenActivities;
    }

    public void setIncludeChildrenActivities(Boolean includeChildrenActivities) {
        this.includeChildrenActivities = includeChildrenActivities;
    }

    public Date getFromDateUpdate() {
        return fromDateUpdate;
    }

    public void setFromDateUpdate(Date fromDateUpdate) {
        this.fromDateUpdate = fromDateUpdate;
    }

    public Date getToDateUpdate() {
        return toDateUpdate;
    }

    public void setToDateUpdate(Date toDateUpdate) {
        this.toDateUpdate = toDateUpdate;
    }
}
