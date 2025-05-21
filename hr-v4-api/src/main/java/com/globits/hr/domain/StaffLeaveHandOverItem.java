package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;

/*
 *  Các hạng mục bàn giao khi nghỉ việc
 */
@Entity
@Table(name = "tbl_staff_leave_handover_item")
public class StaffLeaveHandOverItem extends BaseObject {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_leave_id", nullable = false)
    private StaffLeave staffLeave;

    @Column(name = "display_order")
    private Integer displayOrder; // thứ tự hiển thị

    @Column(name = "name")
    private String name; // tên hạng mục cần bàn giao

    @Column(name = "note")
    private String note; // Ghi chú

    @Column(name = "handover_date")
    private Date handoverDate; // Ngày bàn giao

    @Column(name = "is_handovered")
    private Boolean isHandovered; // Đã nộp hay chưa
    
    

	public StaffLeave getStaffLeave() {
		return staffLeave;
	}

	public void setStaffLeave(StaffLeave staffLeave) {
		this.staffLeave = staffLeave;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getHandoverDate() {
		return handoverDate;
	}

	public void setHandoverDate(Date handoverDate) {
		this.handoverDate = handoverDate;
	}

	public Boolean getIsHandovered() {
		return isHandovered;
	}

	public void setIsHandovered(Boolean isHandovered) {
		this.isHandovered = isHandovered;
	}
    
}
