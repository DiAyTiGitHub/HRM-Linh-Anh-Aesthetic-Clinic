package com.globits.task.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;


@Table(name = "tbl_hr_sub_task_item")
@Entity
public class HrSubTaskItem extends BaseObject {
    /**
     *
     */
    private static final long serialVersionUID = 991992518344617174L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_task_id")
    private HrSubTask subTask;

    @OneToMany(mappedBy = "subTaskItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrSubTaskItemStaff> staffs = new HashSet<HrSubTaskItemStaff>();//Giao việc cho những nhân viên nào

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    private boolean value;

    private Date startTime;

    private Date endTime;

    public HrSubTask getSubTask() {
        return subTask;
    }

    public void setSubTask(HrSubTask subTask) {
        this.subTask = subTask;
    }

    public Set<HrSubTaskItemStaff> getStaffs() {
        return staffs;
    }

    public void setStaffs(Set<HrSubTaskItemStaff> staffs) {
        this.staffs = staffs;
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

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }


}
