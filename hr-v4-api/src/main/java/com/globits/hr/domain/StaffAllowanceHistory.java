package com.globits.hr.domain;

import jakarta.persistence.*;


import com.globits.core.domain.BaseObject;

import java.util.Date;
/*
 * Quá trình phụ cấp
 */

@Table(name = "tbl_staff_allowance_history")
@Entity
public class StaffAllowanceHistory extends BaseObject {
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @ManyToOne
    @JoinColumn(name = "allowance_type_id")
    private AllowanceType allowanceType;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "coefficient")
    private Double coefficient;
    @Column String note;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public AllowanceType getAllowanceType() {
        return allowanceType;
    }

    public void setAllowanceType(AllowanceType allowanceType) {
        this.allowanceType = allowanceType;
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

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
