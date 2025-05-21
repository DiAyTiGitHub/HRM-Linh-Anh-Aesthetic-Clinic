package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

/*
 *  Lần Nghỉ việc của nhân viên
 */
@Entity
@Table(name = "tbl_staff_leave")
public class StaffLeave extends BaseObject {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "staff_id", nullable = false)
	private Staff staff;

	@Column(name = "decision_number")
	private String decisionNumber; // Số quyết định nghỉ việc

	@Column(name = "leave_date")
	private Date leaveDate; // Ngày nghỉ việc

	@Column(name = "still_in_debt")
	private String stillInDebt; // Vẫn còn nợ. VD: Không / Chưa trả máy tính / Nợ tiền thuế

	@Column(name = "paid_status")
	private Integer paidStatus; // Trạng thái chi trả phiếu lương. Chi tiết trong:
	// HrConstants.SalaryResulStaffPaidStatus


	@OneToMany(mappedBy = "staffLeave", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<StaffLeaveHandOverItem> handleOverItems; // Các hạng mục bàn giao



	public Date getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public String getDecisionNumber() {
		return decisionNumber;
	}

	public void setDecisionNumber(String decisionNumber) {
		this.decisionNumber = decisionNumber;
	}

	public String getStillInDebt() {
		return stillInDebt;
	}

	public void setStillInDebt(String stillInDebt) {
		this.stillInDebt = stillInDebt;
	}

	public Set<StaffLeaveHandOverItem> getHandleOverItems() {
		return handleOverItems;
	}

	public void setHandleOverItems(Set<StaffLeaveHandOverItem> handleOverItems) {
		this.handleOverItems = handleOverItems;
	}

	public Integer getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(Integer paidStatus) {
		this.paidStatus = paidStatus;
	}

}
