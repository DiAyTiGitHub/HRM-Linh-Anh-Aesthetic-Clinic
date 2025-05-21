package com.globits.timesheet.domain;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.*;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.domain.WorkingStatus;
import com.globits.task.domain.HrTask;

@Table(name = "tbl_timesheet_detail")
@Entity
public class TimeSheetDetail extends BaseObject {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "staff_work_schedule_id")
	private StaffWorkSchedule staffWorkSchedule;

	@Column(name = "start_time")
	private Date startTime;

	@Column(name = "end_time")
	private Date endTime;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "employee_id", nullable = false)
	private Staff employee;

	@Column(name = "working_item_title")
	private String workingItemTitle;

	@ManyToOne
	@JoinColumn(name = "timesheet_id")
	private TimeSheet timeSheet;

	@Column(name = "hours")
	private double duration;

	@Column(name = "description", columnDefinition = "MEDIUMTEXT")
	private String description;



	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "activity_id")
	private ProjectActivity activity;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "shift_work_time_period_id")
	private ShiftWorkTimePeriod shiftWorkTimePeriod;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "time_sheet_shift_work_period_id")
	private TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod;

	@ManyToOne
	@JoinColumn(name = "working_status_id")
	private WorkingStatus workingStatus;// Trạng thái thực hiện.

	@Column(name = "approve_status")
	private Integer approveStatus;// Trạng thái phê duyệt - 0 = chưa phê duyệt, 1 đã phê duyệt

	@Column(name = "priority")
	private Integer priority;// Sự ưu tiên - 3 = Cao, 2 = trung bình, 1 = thấp, 4 = Gấp

	@ManyToOne
	@JoinColumn(name = "task_id")
	private HrTask task;// Công việc cụ thể nào.

	@Column(name = "note")
	private String note;

	@Column(name = "addressIP_checkIn")
	private String addressIPCheckIn; // địa chỉ IP checkIn

	@Column(name = "addressIP_checkOut")
	private String addressIPCheckOut; // địa chỉ IP checkOut

	@Column(name = "isCurrent")
	private Boolean isCurrent = false; // timeSheetDetail này có đang hoạt động không
	
	@Column(name = "is_sync")
	private Boolean isSync ; //la ban ghi dong bo
	
	@Column(name = "timekeeping_code")
	private String timekeepingCode ;//machamcong

	public String getWorkingItemTitle() {
		return workingItemTitle;
	}

	public void setWorkingItemTitle(String workingItemTitle) {
		this.workingItemTitle = workingItemTitle;
	}

	public TimeSheet getTimeSheet() {
		return timeSheet;
	}

	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
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

	public Staff getEmployee() {
		return employee;
	}

	public void setEmployee(Staff employee) {
		this.employee = employee;
	}

	public ProjectActivity getActivity() {
		return activity;
	}

	public void setActivity(ProjectActivity activity) {
		this.activity = activity;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WorkingStatus getWorkingStatus() {
		return workingStatus;
	}

	public void setWorkingStatus(WorkingStatus workingStatus) {
		this.workingStatus = workingStatus;
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

	public TimeSheetShiftWorkPeriod getTimeSheetShiftWorkPeriod() {
		return timeSheetShiftWorkPeriod;
	}

	public void setTimeSheetShiftWorkPeriod(TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod) {
		this.timeSheetShiftWorkPeriod = timeSheetShiftWorkPeriod;
	}

	public ShiftWorkTimePeriod getShiftWorkTimePeriod() {
		return shiftWorkTimePeriod;
	}

	public void setShiftWorkTimePeriod(ShiftWorkTimePeriod shiftWorkTimePeriod) {
		this.shiftWorkTimePeriod = shiftWorkTimePeriod;
	}

	public HrTask getTask() {
		return task;
	}

	public void setTask(HrTask task) {
		this.task = task;
	}

	// hashCode and equals is used for comparing objects in set (in auto generate
	// timesheet detail function)
	@Override
	public int hashCode() {
		return Objects.hash(this.startTime, this.endTime, this.timeSheet, this.employee);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TimeSheetDetail that))
			return false;
		return Objects.equals(getTimeSheet(), that.getTimeSheet())
				&& Objects.equals(getStartTime(), that.getStartTime())
				&& Objects.equals(getEndTime(), that.getEndTime()) && Objects.equals(getEmployee(), that.getEmployee());
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getAddressIPCheckIn() {
		return addressIPCheckIn;
	}

	public void setAddressIPCheckIn(String addressIPCheckIn) {
		this.addressIPCheckIn = addressIPCheckIn;
	}

	public String getAddressIPCheckOut() {
		return addressIPCheckOut;
	}

	public void setAddressIPCheckOut(String addressIPCheckOut) {
		this.addressIPCheckOut = addressIPCheckOut;
	}

	public StaffWorkSchedule getStaffWorkSchedule() {
		return staffWorkSchedule;
	}

	public void setStaffWorkSchedule(StaffWorkSchedule staffWorkSchedule) {
		this.staffWorkSchedule = staffWorkSchedule;
	}

	public Boolean getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(Boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public Boolean getIsSync() {
		return isSync;
	}

	public void setIsSync(Boolean isSync) {
		this.isSync = isSync;
	}

	public String getTimekeepingCode() {
		return timekeepingCode;
	}

	public void setTimekeepingCode(String timekeepingCode) {
		this.timekeepingCode = timekeepingCode;
	}
	
}
