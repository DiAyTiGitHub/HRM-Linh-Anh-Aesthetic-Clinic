package com.globits.hr.dto.function;

public class DashboardDto {

    private Long staffNumber;

    private Long projectNumber;

    private Long monthTaskNumber;

    public Long getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(Long staffNumber) {
        this.staffNumber = staffNumber;
    }

    public Long getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(Long projectNumber) {
        this.projectNumber = projectNumber;
    }

    public Long getMonthTaskNumber() {
        return monthTaskNumber;
    }

    public void setMonthTaskNumber(Long monthTaskNumber) {
        this.monthTaskNumber = monthTaskNumber;
    }
}
