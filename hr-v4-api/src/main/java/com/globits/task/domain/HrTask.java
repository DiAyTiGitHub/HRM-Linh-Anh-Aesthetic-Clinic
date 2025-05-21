package com.globits.task.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.globits.hr.domain.Staff;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.HrTaskHistory;
import com.globits.hr.domain.WorkingStatus;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.domain.ProjectActivity;


@Table(name = "tbl_hr_task")
@Entity
public class HrTask extends BaseObject {
    /**
     *
     */
    private static final long serialVersionUID = 991992518344617174L;

    //old task - staff relationship
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrTaskStaff> staffs = new HashSet<HrTaskStaff>();// Giao việc cho những nhân viên nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_activity_id")
    private ProjectActivity activity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrSubTask> subTasks;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "estimate_hour")
    private Double estimateHour;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "priority")
    private Integer priority;

    @ManyToOne
    @JoinColumn(name = "working_status_id")
    private WorkingStatus workingStatus;// Trạng thái thực hiện.

    @Column(name = "order_number")
    private Integer orderNumber;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrTaskLabel> labels;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrTaskHistory> historyLogs;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<HrTaskHistory> getHistoryLogs() {
        return historyLogs;
    }

    public void setHistoryLogs(Set<HrTaskHistory> historyLogs) {
        this.historyLogs = historyLogs;
    }

    public Set<HrTaskLabel> getLabels() {
        return labels;
    }

    public void setLabels(Set<HrTaskLabel> labels) {
        this.labels = labels;
    }

    public Set<HrTaskStaff> getStaffs() {
        return staffs;
    }

    public void setStaffs(Set<HrTaskStaff> staffs) {
        this.staffs = staffs;
    }

    public ProjectActivity getActivity() {
        return activity;
    }

    public void setActivity(ProjectActivity activity) {
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

    public WorkingStatus getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(WorkingStatus workingStatus) {
        this.workingStatus = workingStatus;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Set<HrSubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(Set<HrSubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
