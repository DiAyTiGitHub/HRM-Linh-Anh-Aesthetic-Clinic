package com.globits.hr.dto.function;

import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.domain.ProjectStaff;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectTimeWorkTableDto {
    private UUID projectId;
    private String project;
    private int numberStaff;
    private double totalHours;
    private List<StaffDto> projectStaff;
    private List<Double> data;

    public ProjectTimeWorkTableDto(Project entity) {
        this.projectId = entity.getId();
        this.project = entity.getName();
        if(entity.getProjectStaff()!=null && entity.getProjectStaff().size()>0) {
        	this.numberStaff = entity.getProjectStaff().size();
        }        
        this.projectStaff = new ArrayList<>();
        if (entity.getProjectStaff() != null && !entity.getProjectStaff().isEmpty()) {
            for (ProjectStaff projectstaff : entity.getProjectStaff()) {
                this.projectStaff.add(new StaffDto(projectstaff.getStaff(),true));
            }
        }
    }

    public ProjectTimeWorkTableDto(ProjectStaff entity) {
        this.projectId = entity.getProject().getId();
        this.project = entity.getProject().getName();
        this.numberStaff = entity.getProject().getProjectStaff().size();
    }

    public List<StaffDto> getProjectStaff() {
        return projectStaff;
    }

    public void setProjectStaff(List<StaffDto> projectStaff) {
        this.projectStaff = projectStaff;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public int getNumberStaff() {
        return numberStaff;
    }

    public void setNumberStaff(int numberStaff) {
        this.numberStaff = numberStaff;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }
}
