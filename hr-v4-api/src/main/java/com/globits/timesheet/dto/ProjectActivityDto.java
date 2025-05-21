package com.globits.timesheet.dto;

import java.util.*;

import com.globits.core.dto.BaseObjectDto;
import com.globits.timesheet.domain.ProjectActivity;

public class ProjectActivityDto extends BaseObjectDto {
    private ProjectDto project;

    private UUID projectId;
    private String projectName;
    private String code;
    private String name;
    private String description;
    private UUID parentId;
    private Date startTime;
    private Date endTime;
    private Double duration;
    private Double estimateDuration;
    private ProjectActivityDto parent;
    private List<ProjectActivityDto> child;

    public ProjectActivityDto() {
    }

    public ProjectActivityDto(ProjectActivity entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.code = entity.getCode();
            this.name = entity.getName();
            this.description = entity.getDescription();
            this.startTime = entity.getStartTime();
            this.endTime = entity.getEndTime();
            this.duration = entity.getDuration();
            this.estimateDuration = entity.getEstimateDuration();
            if (entity.getProject() != null) {
                ProjectDto projectDto = new ProjectDto();
                projectDto.setId(entity.getProject().getId());
                projectDto.setName(entity.getProject().getName());
                this.project = projectDto;
            }
            if (entity.getParent() != null) {
                this.parent = new ProjectActivityDto();
                this.parent.setId(entity.getParent().getId());
                this.parent.setCode(entity.getParent().getCode());
                this.parent.setName(entity.getParent().getName());
                this.parent.setDescription(entity.getParent().getDescription());
                this.setParentId(this.parent.getId());
            }
            if (entity.getChild() != null && !entity.getChild().isEmpty()) {
                this.child = new ArrayList<>();
                List<ProjectActivityDto> activityList = new ArrayList<>();
                ProjectActivityDto activityDto = null;
                for (ProjectActivity child : entity.getChild()) {
                    activityDto = new ProjectActivityDto();
                    activityDto.setId(child.getId());
                    activityDto.setCode(child.getCode());
                    activityDto.setName(child.getName());
                    activityDto.setEstimateDuration(child.getEstimateDuration());
                    activityDto.setDuration(child.getDuration());
                    activityDto.setStartTime(child.getStartTime());
                    activityDto.setEndTime(child.getEndTime());
                    if (child.getParent() != null) {
                        activityDto.setParentId(child.getParent().getId());
                    }

                    activityList.add(activityDto);
                }
                if (activityList != null && activityList.size() > 0) {
                    activityList.sort(Comparator.comparing(ProjectActivityDto::getCode,
                            Comparator.nullsFirst(Comparator.reverseOrder())));
                    this.child.addAll(activityList);
                }
            }
        }
    }

    public ProjectActivityDto(ProjectActivity entity, boolean recursive) {
        if (entity != null) {
            this.id = entity.getId();
            this.code = entity.getCode();
            this.name = entity.getName();
            this.description = entity.getDescription();
            this.startTime = entity.getStartTime();
            this.endTime = entity.getEndTime();
            this.duration = entity.getDuration();
            this.estimateDuration = entity.getEstimateDuration();

            if (entity.getProject() != null) {
                this.projectId = entity.getProject().getId();
                // this.projectName = entity.getProject().getName();
            }

            if (recursive) {
                if (entity.getParent() != null) {
                    this.setParentId(entity.getParent().getId());
                }

                if (entity.getChild() != null && entity.getChild().size() > 0) {
                    List<ProjectActivityDto> activities = new ArrayList<>();
                    for (ProjectActivity pa : entity.getChild()) {
                        activities.add(new ProjectActivityDto(pa, true));
                    }
                    if (activities != null && activities.size() > 0) {
                        this.child = new ArrayList<>();
                        activities.sort(Comparator.comparing(ProjectActivityDto::getCode,
                                Comparator.nullsFirst(Comparator.reverseOrder())));
                        this.child.addAll(activities);
                    }
                }
            }
        }

    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public ProjectActivityDto getParent() {
        return parent;
    }

    public void setParent(ProjectActivityDto parent) {
        this.parent = parent;
    }

    public List<ProjectActivityDto> getChild() {
        return child;
    }

    public void setChild(List<ProjectActivityDto> child) {
        this.child = child;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Double getEstimateDuration() {
        return estimateDuration;
    }

    public void setEstimateDuration(Double estimateDuration) {
        this.estimateDuration = estimateDuration;
    }

}
