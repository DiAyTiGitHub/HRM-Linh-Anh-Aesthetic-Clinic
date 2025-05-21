package com.globits.timesheet.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.ProjectStaff;

public class ProjectStaffDto extends BaseObjectDto {
    private StaffDto staff;
    private ProjectDto project;

    public ProjectStaffDto() {
        super();
    }

    public ProjectStaffDto(ProjectStaff entity) {
        if (entity != null) {
            if (entity.getStaff() != null) {
                StaffDto staffDto = new StaffDto();
                staffDto.setId(entity.getStaff().getId());
                staffDto.setDisplayName(entity.getStaff().getDisplayName());
                this.staff = staffDto;
            }
            if (entity.getProject() != null) {
                ProjectDto projectDto = new ProjectDto();
                projectDto.setId(entity.getProject().getId());
                projectDto.setName(entity.getProject().getName());
                this.project = projectDto;
            }
        }
    }


    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

}
