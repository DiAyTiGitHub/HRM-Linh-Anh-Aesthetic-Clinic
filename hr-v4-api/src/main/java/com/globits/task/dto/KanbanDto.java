package com.globits.task.dto;

import java.util.Date;
import java.util.UUID;

import com.globits.core.utils.CoreDateTimeUtil;
import com.globits.hr.domain.Staff;
import com.globits.task.domain.HrTask;

public class KanbanDto {

    private UUID id;
    private String assignee;
    private String activity;
    private String projectName;
    private String projectCode;
    private String name;
    private String code;
    private Date startTime;
    private Date endTime;
    private UUID statusId;
    private String statusName;
    private Integer priority;
    private Date lastModifyDate;

    private Date createDate;

    private String creatorName;

    public KanbanDto() {
    }

    public KanbanDto(HrTask task) {
        if (task != null) {
            this.id = task.getId();
            this.name = task.getName();
            this.code = task.getCode();
            this.startTime = task.getStartTime();
            this.endTime = task.getEndTime();
            this.priority = task.getPriority();
            if (task.getModifyDate() != null) {
                this.lastModifyDate = CoreDateTimeUtil.convertToDateViaInstant(task.getModifyDate());
            }
            // assignee
            if (task.getStaffs() != null && task.getStaffs().size() > 0) {
                Staff firstStaff = task.getStaffs().iterator().next().getStaff();
                if (firstStaff != null) {
                    this.assignee = firstStaff.getDisplayName();
                }
            }
            // Activity
            if (task.getActivity() != null) {
                this.activity = task.getActivity().getName();
            }
            // status
            if (task.getWorkingStatus() != null) {
                this.statusName = task.getWorkingStatus().getName();
                this.statusId = task.getWorkingStatus().getId();
            }
            // project
            if (task.getProject() != null) {
                this.projectCode = task.getProject().getCode();
                this.projectName = task.getProject().getName();
            }
            //create date
            if (task.getCreateDate() != null)
                this.createDate = CoreDateTimeUtil.convertToDateViaInstant(task.getCreateDate());

            //default createBy is error
//            if (task.getCreatedBy() != null && !task.getCreatedBy().equals("admin")) {
//                this.creatorName = task.getCreatedBy();
//            }
        }
    }

    public KanbanDto(HrTaskDto taskDto) {
        if (taskDto != null) {
            this.id = taskDto.getId();
            this.name = taskDto.getName();
            this.code = taskDto.getCode();
            this.startTime = taskDto.getStartTime();
            this.endTime = taskDto.getEndTime();
            this.priority = taskDto.getPriority();
            if (taskDto.getModifyDate() != null) {
                this.lastModifyDate = CoreDateTimeUtil.convertToDateViaInstant(taskDto.getModifyDate());
            }
            // assignee
            if (taskDto.getAssignee() != null) {
                    this.assignee = taskDto.getAssignee().getDisplayName();
            }
            // Activity
            if (taskDto.getActivity() != null) {
                this.activity = taskDto.getActivity().getName();
            }
            // status
            if (taskDto.getStatus() != null) {
                this.statusName = taskDto.getStatus().getName();
                this.statusId = taskDto.getStatus().getId();
            }
            // project
            if (taskDto.getProject() != null) {
                this.projectCode = taskDto.getProject().getCode();
                this.projectName = taskDto.getProject().getName();
            }
            //create date
            if (taskDto.getCreateDate() != null)
                this.createDate = CoreDateTimeUtil.convertToDateViaInstant(taskDto.getCreateDate());

        }
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
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

    public UUID getStatusId() {
        return statusId;
    }

    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
