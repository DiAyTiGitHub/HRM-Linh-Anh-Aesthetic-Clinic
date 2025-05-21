package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.task.domain.HrTask;

import jakarta.persistence.*;



@Table(name = "tbl_task_history")
@Entity
public class HrTaskHistory extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private HrTask task;

    private String code;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String event;

    @ManyToOne
    @JoinColumn(name = "modifier_id")
    private Staff modifier;

    public HrTask getTask() {
        return task;
    }

    public void setTask(HrTask task) {
        this.task = task;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Staff getModifier() {
        return modifier;
    }

    public void setModifier(Staff modifier) {
        this.modifier = modifier;
    }
}
