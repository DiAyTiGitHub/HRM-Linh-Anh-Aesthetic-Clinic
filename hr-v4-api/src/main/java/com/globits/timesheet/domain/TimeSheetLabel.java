package com.globits.timesheet.domain;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Table(name = "tbl_timesheet_label")
@Entity
public class TimeSheetLabel extends BaseObject{
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "timesheet_id")
    private TimeSheet timesheet;

    @ManyToOne
	@JoinColumn(name="label_id")
	private Label label;

    public TimeSheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(TimeSheet timesheet) {
        this.timesheet = timesheet;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    
}
