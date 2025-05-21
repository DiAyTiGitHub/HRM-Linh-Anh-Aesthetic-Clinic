package com.globits.task.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Staff;


@Table(name = "tbl_hr_sub_task_item_staff", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "staff_id", "hr_sub_task_item_id" }) })
@Entity
public class HrSubTaskItemStaff extends BaseObject {
	private static final long serialVersionUID = -987880391142046683L;

	/**
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "staff_id")
	private Staff staff;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "hr_sub_task_item_id")
	private HrSubTaskItem subTaskItem;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public HrSubTaskItem getSubTaskItem() {
		return subTaskItem;
	}

	public void setSubTaskItem(HrSubTaskItem subTaskItem) {
		this.subTaskItem = subTaskItem;
	}

}
