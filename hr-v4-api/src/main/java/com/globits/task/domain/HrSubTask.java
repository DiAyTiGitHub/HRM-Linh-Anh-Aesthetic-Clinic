package com.globits.task.domain;

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


@Table(name = "tbl_hr_sub_task")
@Entity
public class HrSubTask extends BaseObject {
    /**
     *
     */
    private static final long serialVersionUID = 991992518344617174L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private HrTask task;

    @OneToMany(mappedBy = "subTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrSubTaskItem> subTaskItems;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public HrTask getTask() {
        return task;
    }

    public void setTask(HrTask task) {
        this.task = task;
    }

    public Set<HrSubTaskItem> getSubTaskItems() {
        return subTaskItems;
    }

    public void setSubTaskItems(Set<HrSubTaskItem> subTaskItems) {
        this.subTaskItems = subTaskItems;
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


}
