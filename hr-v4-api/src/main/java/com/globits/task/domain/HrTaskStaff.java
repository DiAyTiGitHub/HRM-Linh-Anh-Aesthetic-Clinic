package com.globits.task.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.globits.core.domain.BaseObject;
import com.globits.hr.data.types.HrTaskStatus;
import com.globits.hr.domain.Staff;


@Table(name = "tbl_hr_task_staff",uniqueConstraints = { @UniqueConstraint(columnNames = { "staff_id", "hr_task_id" }) })
@Entity	
public class HrTaskStaff extends BaseObject {
	private static final long serialVersionUID = -987880391142046683L;

	/**
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hr_task_id")
    private HrTask task;

	@Column(name="status")
    @Enumerated(value = EnumType.STRING)
	private HrTaskStatus status;//Trạng thái: mới tại, đã nhận việc, đang làm việc, đã hoàn thành, đã hủy bỏ
	
	@Column(name="date_assign")
	private Date dateAssign;//Ngày giao việc
	
	@Column(name="date_finished")
	private Date dateFinished;//Ngày kết thúc việc
	
	@Column(name="role")
	private String role;//Vai trò trong công việc: chỉ đạo chung, hỗ trợ, test, làm chính...
	
	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public HrTask getTask() {
		return task;
	}

	public void setTask(HrTask task) {
		this.task = task;
	}

	public HrTaskStatus getStatus() {
		return status;
	}

	public void setStatus(HrTaskStatus status) {
		this.status = status;
	}

	public Date getDateAssign() {
		return dateAssign;
	}

	public void setDateAssign(Date dateAssign) {
		this.dateAssign = dateAssign;
	}

	public Date getDateFinished() {
		return dateFinished;
	}

	public void setDateFinished(Date dateFinished) {
		this.dateFinished = dateFinished;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
