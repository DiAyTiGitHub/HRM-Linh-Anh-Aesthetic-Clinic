package com.globits.timesheet.dto;

import com.globits.hr.utils.DateTimeUtil;
import com.globits.timesheet.domain.TimeSheetDetail;

import java.util.Date;
import java.util.UUID;

public class TimeSheetCalendarItemDto {

    private UUID id;
    private Date workingDate;
    private UUID staffId;
    private String staffName;
    private Date startTime;
    private Date endTime;
    private double duration;
    private UUID activityId;
    private String activity;
    private UUID projectId;
    private UUID taskId;
    private String task;
    private String project;
    private String description;
    private Integer approveStatus;
    private Integer priority;
    private String workingStatus;

    public TimeSheetCalendarItemDto() {

    }

    public TimeSheetCalendarItemDto(TimeSheetDetail entity) {
        if (entity == null) return;

        this.id = entity.getId();
        this.workingDate = DateTimeUtil.setTime(entity.getTimeSheet().getWorkingDate(), 0, 0, 0, 0);

        if (entity.getEmployee() != null) {
            this.staffId = entity.getEmployee().getId();
            this.staffName = entity.getEmployee().getDisplayName();
        }
        this.startTime = entity.getStartTime();
        this.endTime = entity.getEndTime();
        this.duration = entity.getDuration();

        if (entity.getActivity() != null) {
            this.activityId = entity.getActivity().getId();
            this.activity = entity.getActivity().getName();
        } else {
            this.activityId = null;
            this.activity = "Chưa ghi nhận hoạt động";
        }

        if (entity.getProject() != null) {
            this.projectId = entity.getProject().getId();
            this.project = entity.getProject().getName();
        } else {
            this.projectId = null;
            this.project = "Chưa ghi nhận dự án";
        }

        if (entity.getTask() != null) {
            this.taskId = entity.getTask().getId();
            this.task = entity.getTask().getName();
        } else {
            this.taskId = null;
            this.task = "Chưa ghi nhận phần việc";
        }

        this.description = entity.getDescription();
        this.approveStatus = entity.getApproveStatus();
        this.priority = entity.getPriority();
        if (entity.getWorkingStatus() != null) {
            this.workingStatus = entity.getWorkingStatus().getName();
        }
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(String workingStatus) {
        this.workingStatus = workingStatus;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public UUID getActivityId() {
        return activityId;
    }

    public void setActivityId(UUID activityId) {
        this.activityId = activityId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

}
