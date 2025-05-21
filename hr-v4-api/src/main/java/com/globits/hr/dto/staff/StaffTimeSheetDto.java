package com.globits.hr.dto.staff;

public class StaffTimeSheetDto {
    private String projectId;
    private String projectName;

    private Double totalTime;
    private Double coefficientsSalary;
    private Double provisionalSalary;

    public StaffTimeSheetDto(){

    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }

    public Double getCoefficientsSalary() {
        return coefficientsSalary;
    }

    public void setCoefficientsSalary(Double coefficientsSalary) {
        this.coefficientsSalary = coefficientsSalary;
    }

    public Double getProvisionalSalary() {
        return provisionalSalary;
    }

    public void setProvisionalSalary(Double provisionalSalary) {
        this.provisionalSalary = provisionalSalary;
    }
}
