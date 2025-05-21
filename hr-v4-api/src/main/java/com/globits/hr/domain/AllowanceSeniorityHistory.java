package com.globits.hr.domain;
/*
 * lịch sử các thay đổi về phụ cấp thâm niên của nhân viên
 */
import com.globits.core.domain.BaseObject;

import jakarta.persistence.*;

import java.util.Date;
@Entity
@Table(name = "tbl_allowance_seniority_history")

public class AllowanceSeniorityHistory extends BaseObject {
    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "civil_servant_category_id")
    private CivilServantCategory quotaCode;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "percent_received")
    private Double percentReceived;
    @Column(name = "note")
    private String note;

    public CivilServantCategory getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(CivilServantCategory quotaCode) {
        this.quotaCode = quotaCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Double getPercentReceived() {
        return percentReceived;
    }

    public void setPercentReceived(Double percentReceived) {
        this.percentReceived = percentReceived;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
