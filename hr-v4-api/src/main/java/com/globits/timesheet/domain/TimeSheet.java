package com.globits.timesheet.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.globits.hr.domain.TimeSheetStaff;
import com.globits.core.domain.BaseObject;
import com.globits.hr.data.types.TimeSheetRegStatus;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.domain.WorkingStatus;


@Table(name = "tbl_timesheet")
@Entity
public class TimeSheet extends BaseObject {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne (cascade= CascadeType.PERSIST)
	@JoinColumn(name="staff_id")
	private Staff staff;
	
	/*
	 * Timesheet nay cham cho Schedule nao
	 */
	@ManyToOne
	@JoinColumn(name="schedule_id")
	private StaffWorkSchedule schedule;
	
	@OneToMany(mappedBy = "timeSheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TimeSheetDetail> details;
	
	@Column(name="working_date",nullable = false)
	private Date workingDate;

	// Tổng thời gian làm việc 
	// (không nhất thiết phải là giờ kết thúc - giờ bắt đầu vì còn vấn đề nghỉ giữa giờ, ra ngoài, ...)
	@Column(name ="total_hours")
	private double 	totalHours;
	
	@Column(name="start_time")
	private Date startTime;//Thời điểm bắt đầu làm việc
	@Column(name="end_time")
	private Date endTime;//Thời điểm kết thúc công việc
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "year")
	private Integer year;

	@Column(name = "month")
	private Integer month;

	@Column(name = "day")
	private  Integer day;

	@ManyToOne (cascade= CascadeType.PERSIST)
	@JoinColumn(name="project_id")
	private Project project;//Thuộc Project nào
	
	@ManyToOne (cascade= CascadeType.PERSIST)
	@JoinColumn(name="activity_id")
	private ProjectActivity activity;//Cho activity nào của project (có thể dùng sau)
	
//	@ManyToOne (cascade= CascadeType.PERSIST)
//	@JoinColumn(name="shift_work_id")
//	private ShiftWork shiftWork;

	@ManyToOne
	@JoinColumn(name="working_status_id")
	private WorkingStatus workingStatus;//Trạng thái thực hiện.
	
	@Column(name ="approve_status")
	private Integer approveStatus;//Trạng thái phê duyệt - 0 = chưa phê duyệt, 1 đã phê duyệt
	@Column(name ="priority")
	private Integer priority;//Sự ưu tiên - 3 = Cao, 2 = trung bình, 1 = thấp, 4 = Gấp

	@OneToMany(mappedBy = "timesheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TimeSheetStaff> timeSheetStaffSet;
	
	@OneToMany(mappedBy = "timeSheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TimeSheetShiftWorkPeriod> timeSheetShiftWorkPeriod;

	@OneToMany(mappedBy = "timesheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TimeSheetLabel> labels;
	
	@Column(name="reg_status")
	@Enumerated(value=EnumType.STRING)
	private TimeSheetRegStatus regStatus;

	public StaffWorkSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(StaffWorkSchedule schedule) {
		this.schedule = schedule;
	}

	public Date getWorkingDate() {
		return workingDate;
	}

	public void setWorkingDate(Date workingDate) {
		this.workingDate = workingDate;
	}

	public double getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(double totalHours) {
		this.totalHours = totalHours;
	}
	
	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
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

	public Integer getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(Integer approveStatus) {
		this.approveStatus = approveStatus;
	}

	public Set<TimeSheetDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<TimeSheetDetail> details) {
	    if (this.details == null) {
	        this.details = new HashSet<>();
	    }
	    if (details == null) {
	        this.details.clear();
	    } else {
	        this.details.clear();
	        this.details.addAll(details);
	        for (TimeSheetDetail detail : this.details) {
	            detail.setTimeSheet(this);
	        }
	    }
	}
	
	public void addDetail(TimeSheetDetail detail) {
        this.details.add(detail);
        detail.setTimeSheet(this);
    }
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public ProjectActivity getActivity() {
		return activity;
	}

	public void setActivity(ProjectActivity activity) {
		this.activity = activity;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Set<TimeSheetStaff> getTimeSheetStaffSet() {
		return timeSheetStaffSet;
	}

	public void setTimeSheetStaffSet(Set<TimeSheetStaff> timeSheetStaffSet) {
		this.timeSheetStaffSet = timeSheetStaffSet;
	}

	public Set<TimeSheetShiftWorkPeriod> getTimeSheetShiftWorkPeriod() {
		return timeSheetShiftWorkPeriod;
	}

	public void setTimeSheetShiftWorkPeriod(Set<TimeSheetShiftWorkPeriod> timeSheetShiftWorkPeriod) {
		this.timeSheetShiftWorkPeriod = timeSheetShiftWorkPeriod;
	}

	public Set<TimeSheetLabel> getLabels() {
		return labels;
	}

	public void setLabels(Set<TimeSheetLabel> labels) {
		this.labels = labels;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public TimeSheetRegStatus getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(TimeSheetRegStatus regStatus) {
		this.regStatus = regStatus;
	}
}
