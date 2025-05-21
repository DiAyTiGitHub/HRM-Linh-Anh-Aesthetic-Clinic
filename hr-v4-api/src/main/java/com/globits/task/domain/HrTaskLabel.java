package com.globits.task.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;
import com.globits.timesheet.domain.Label;


@Table(name = "tbl_hr_task_label")
@Entity
public class HrTaskLabel extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "hr_task_id")
    private HrTask task;

    @ManyToOne
    @JoinColumn(name = "label_id")
    private Label label;

    public HrTask getTask() {
        return task;
    }

    public void setTask(HrTask task) {
        this.task = task;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
