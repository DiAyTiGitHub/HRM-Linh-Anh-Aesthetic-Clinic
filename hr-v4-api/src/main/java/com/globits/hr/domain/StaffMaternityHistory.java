package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.*;

import java.util.Date;

/*
 *Lịch sử thai sản
 */
@Entity
@Table(name = "tbl_staff_maternity_history")

public class StaffMaternityHistory extends BaseObject {
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    // Ngày bắt đầu hưởng chế độ thai sản
    @Column(name = "start_date")
    private Date startDate;

    // Ngày kết thúc hưởng chế độ thai san
    @Column(name = "end_date")
    private Date endDate;

    // Ngày bắt đầu nghỉ thai sản
    @Column(name = "maternity_leave_start_date")
    private Date maternityLeaveStartDate;

    // Ngày kết thúc nghỉ thai sản
    @Column(name = "maternity_leave_end_date")
    private Date maternityLeaveEndDate;

    @Column(name = "birth_number")
    private Integer birthNumber;

    @Column(name = "note")
    private String note;


    public Date getMaternityLeaveStartDate() {
        return maternityLeaveStartDate;
    }

    public void setMaternityLeaveStartDate(Date maternityLeaveStartDate) {
        this.maternityLeaveStartDate = maternityLeaveStartDate;
    }

    public Date getMaternityLeaveEndDate() {
        return maternityLeaveEndDate;
    }

    public void setMaternityLeaveEndDate(Date maternityLeaveEndDate) {
        this.maternityLeaveEndDate = maternityLeaveEndDate;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getBirthNumber() {
        return birthNumber;
    }

    public void setBirthNumber(Integer birthNumber) {
        this.birthNumber = birthNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
