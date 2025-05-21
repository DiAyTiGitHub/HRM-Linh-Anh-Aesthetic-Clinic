package com.globits.timesheet.domain;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.globits.core.domain.BaseObject;
import com.globits.hr.data.types.TimeSheetRegStatus;
import com.globits.hr.domain.ShiftWorkTimePeriod;



@Table(name = "tbl_timesheet_shiftWork_period")
@Entity
public class TimeSheetShiftWorkPeriod extends BaseObject {
	private static final long serialVersionUID = 1L;

	@ManyToOne (cascade= CascadeType.PERSIST)
	@JoinColumn(name="shift_work_time_period_id")
	private ShiftWorkTimePeriod shiftWorkTimePeriod;

	@ManyToOne (cascade= CascadeType.PERSIST)
	@JoinColumn(name="timeSheet_id")
	private TimeSheet timeSheet;
	
	@Column(name="note")
	private String note;
	
	//Quy định tại HrConstants.WorkingFormatEnum
	@Column(name="working_format")
	private Integer workingFormat;
	
	@Column(name="reg_status")
	@Enumerated(value=EnumType.STRING)
	private TimeSheetRegStatus regStatus;

	public ShiftWorkTimePeriod getShiftWorkTimePeriod() {
		return shiftWorkTimePeriod;
	}

	public void setShiftWorkTimePeriod(ShiftWorkTimePeriod shiftWorkTimePeriod) {
		this.shiftWorkTimePeriod = shiftWorkTimePeriod;
	}

	public TimeSheet getTimeSheet() {
		return timeSheet;
	}

	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getWorkingFormat() {
		return workingFormat;
	}

	public void setWorkingFormat(Integer workingFormat) {
		this.workingFormat = workingFormat;
	}

	public TimeSheetRegStatus getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(TimeSheetRegStatus regStatus) {
		this.regStatus = regStatus;
	}
	
}
