package com.globits.timesheet.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.Label;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.domain.ProjectStaff;

public class ProjectDto extends BaseObjectDto {
    private String name;
    private String code;
    private String description;
    private Double workload;// Tổng số giờ làm việc theo kế hoạch
    private Double actualWorkload;// Tổng số giờ làm việc hiện thời (tính tổng giờ của Timesheet)
    private List<StaffDto> projectStaff;
    private List<LabelDto> labels;
    private Date startDate;
    private Date endDate;
    private Boolean isFinished;

    public ProjectDto() {
        super();
    }

    public ProjectDto(Project entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
            this.workload = entity.getWorkload();
            this.actualWorkload = entity.getActualWorkload();
            this.startDate = entity.getStartDate();
            this.endDate = entity.getEndDate();
            this.isFinished = entity.isFinished();
            if (entity.getProjectStaff() != null && !entity.getProjectStaff().isEmpty()) {
                this.projectStaff = new ArrayList<>();
                for (ProjectStaff projectstaff : entity.getProjectStaff()) {
                    if (projectstaff.getVoided() != null && projectstaff.getVoided())
                        continue;
                    StaffDto staffDto = new StaffDto();
                    staffDto.setId(projectstaff.getStaff().getId());
                    staffDto.setDisplayName(projectstaff.getStaff().getDisplayName());
                    this.projectStaff.add(staffDto);
                }
            }
            if (entity.getLabels() != null && !entity.getLabels().isEmpty()) {
                this.labels = new ArrayList<>();
                for (Label label : entity.getLabels()) {
                    LabelDto labelDto = new LabelDto();
                    labelDto.setId(label.getId());
                    labelDto.setName(label.getName());
                    labelDto.setColor(label.getColor());
                    this.labels.add(labelDto);
                }
            }
        }
    }

    public ProjectDto(Project entity, boolean collapse) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
            this.workload = entity.getWorkload();
            this.actualWorkload = entity.getActualWorkload();
            this.startDate = entity.getStartDate();
            this.endDate = entity.getEndDate();
            this.isFinished = entity.isFinished();
            if (!collapse) {
                if (collapse && entity.getProjectStaff() != null && !entity.getProjectStaff().isEmpty()) {
                    this.projectStaff = new ArrayList<>();
                    for (ProjectStaff projectstaff : entity.getProjectStaff()) {
                        StaffDto staffDto = new StaffDto();
                        staffDto.setId(projectstaff.getStaff().getId());
                        staffDto.setDisplayName(projectstaff.getStaff().getDisplayName());
                        this.projectStaff.add(staffDto);
                    }
                }
                if (entity.getLabels() != null && !entity.getLabels().isEmpty()) {
                    this.labels = new ArrayList<>();
                    for (Label label : entity.getLabels()) {
                        LabelDto labelDto = new LabelDto();
                        labelDto.setId(label.getId());
                        labelDto.setName(label.getName());
                        labelDto.setColor(label.getColor());
                        this.labels.add(labelDto);
                    }
                }
            }
        }
    }

    public List<LabelDto> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelDto> labels) {
        this.labels = labels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getWorkload() {
        return workload;
    }

    public void setWorkload(Double workload) {
        this.workload = workload;
    }

    public Double getActualWorkload() {
        return actualWorkload;
    }

    public void setActualWorkload(Double actualWorkload) {
        this.actualWorkload = actualWorkload;
    }

    public List<StaffDto> getProjectStaff() {
        return projectStaff;
    }

    public void setProjectStaff(List<StaffDto> projectStaff) {
        this.projectStaff = projectStaff;
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

    public Boolean isFinished() {
        return isFinished;
    }

    public void setFinished(Boolean isFinished) {
        this.isFinished = isFinished;
    }
}
