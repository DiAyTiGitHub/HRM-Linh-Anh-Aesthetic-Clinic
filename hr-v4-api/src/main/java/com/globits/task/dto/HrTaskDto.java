package com.globits.task.dto;

import com.globits.hr.domain.Staff;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.utils.CoreDateTimeUtil;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.WorkingStatusDto;
import com.globits.hr.dto.comparator.HrSubTaskComparator;
import com.globits.task.domain.HrSubTask;
import com.globits.task.domain.HrTask;
import com.globits.task.domain.HrTaskLabel;
import com.globits.timesheet.dto.LabelDto;
import com.globits.timesheet.dto.ProjectActivityDto;
import com.globits.timesheet.dto.ProjectDto;

import java.util.*;

public class HrTaskDto extends BaseObjectDto {
    // old field of staffs
    // private List<StaffDto> staffs;
    // new field of staff, only 1 person does the task at certain time
    private StaffDto assignee;
    private List<HrTaskStaffDto> taskStaffs;
    private ProjectActivityDto activity;
    private ProjectDto project;
    private String name;
    private String code;
    private String description;
    private Double estimateHour;
    private Date startTime;
    private Date endTime;
    private WorkingStatusDto status;
    private Integer orderNumber;
    private Integer priority;
    private Date lastModifyDate;

    private List<HrSubTaskDto> subTasks;
    private List<LabelDto> labels;

    private String comment;

    public HrTaskDto() {
    }

    public HrTaskDto(HrTask entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
            this.estimateHour = entity.getEstimateHour();
            this.startTime = entity.getStartTime();
            this.endTime = entity.getEndTime();
            this.orderNumber = entity.getOrderNumber();
            this.priority = entity.getPriority();
            this.comment = entity.getComment();
            if (entity.getModifyDate() != null) {
                this.lastModifyDate = CoreDateTimeUtil.convertToDateViaInstant(entity.getModifyDate());
            }

            if (entity.getStaffs() != null && entity.getStaffs().size() > 0) {
                Staff firstStaff = entity.getStaffs().iterator().next().getStaff();
                if (firstStaff != null) {
                    StaffDto staff = new StaffDto();
                    staff.setId(firstStaff.getId());
                    staff.setDisplayName(firstStaff.getDisplayName());
                    staff.setStaffCode(firstStaff.getStaffCode());
                    this.assignee = staff;
                }
            }

            if (entity.getActivity() != null) {
                ProjectActivityDto projectActivityDto = new ProjectActivityDto();
                projectActivityDto.setId(entity.getActivity().getId());
                projectActivityDto.setName(entity.getActivity().getName());
                this.activity = projectActivityDto;
            }
            if (entity.getWorkingStatus() != null) {
                WorkingStatusDto statusDto = new WorkingStatusDto();
                statusDto.setId(entity.getWorkingStatus().getId());
                statusDto.setName(entity.getWorkingStatus().getName());
                this.status = statusDto;
            }
            if (entity.getProject() != null) {
                ProjectDto projectDto = new ProjectDto();
                projectDto.setId(entity.getProject().getId());
                projectDto.setName(entity.getProject().getName());
                projectDto.setCode(entity.getProject().getCode());
                this.project = projectDto;
            }
            if (entity.getSubTasks() != null && entity.getSubTasks().size() > 0) {
                this.subTasks = new ArrayList<>();
                List<HrSubTaskDto> subTaskList = new ArrayList<>();
                for (HrSubTask subTask : entity.getSubTasks()) {
                    subTaskList.add(new HrSubTaskDto(subTask));
                }
                if (subTaskList != null && subTaskList.size() > 0) {
//                    subTaskList.sort(Comparator.comparing(HrSubTaskDto::getName,
//                            Comparator.nullsFirst(Comparator.naturalOrder())));

                    //new comparator
                    Collections.sort(subTaskList, new HrSubTaskComparator());
                    this.subTasks.addAll(subTaskList);
                }
            }
            if (entity.getLabels() != null) {
                this.labels = new ArrayList<>();
                for (HrTaskLabel taskLabel : entity.getLabels()) {
                    this.labels.add(new LabelDto(taskLabel.getLabel(), false));
                }
            }
        }
    }

    public ProjectActivityDto getActivity() {
        return activity;
    }

    public void setActivity(ProjectActivityDto activity) {
        this.activity = activity;
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

    public Double getEstimateHour() {
        return estimateHour;
    }

    public void setEstimateHour(Double estimateHour) {
        this.estimateHour = estimateHour;
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

    public WorkingStatusDto getStatus() {
        return status;
    }

    public void setStatus(WorkingStatusDto status) {
        this.status = status;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    // public List<StaffDto> getStaffs() {
    // return staffs;
    // }
    //
    // public void setStaffs(List<StaffDto> staffs) {
    // this.staffs = staffs;
    // }

    public List<HrTaskStaffDto> getTaskStaffs() {
        return taskStaffs;
    }

    public void setTaskStaffs(List<HrTaskStaffDto> taskStaffs) {
        this.taskStaffs = taskStaffs;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<HrSubTaskDto> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<HrSubTaskDto> subTasks) {
        this.subTasks = subTasks;
    }

    public List<LabelDto> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelDto> labels) {
        this.labels = labels;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public StaffDto getAssignee() {
        return assignee;
    }

    public void setAssignee(StaffDto assignee) {
        this.assignee = assignee;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }
}
