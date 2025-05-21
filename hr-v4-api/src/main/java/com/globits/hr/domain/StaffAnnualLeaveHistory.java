package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Person;
import jakarta.persistence.*;

import java.util.Set;

// Thống kê ngày nghỉ phép theo năm của nhân viên
@Entity
@Table(name = "tbl_staff_annual_leave_history")
public class StaffAnnualLeaveHistory extends BaseObject {
    private static final long serialVersionUID = 6014783475303579207L;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff; // Nhân viên nào nghỉ phép

    @Column(name = "year", nullable = false)
    private Integer year; // Năm thống kê nghỉ phép

    @Column(name = "granted_leave_days")
    private Double grantedLeaveDays; // Số ngày nghỉ phép được cấp trong năm

    @Column(name = "granted_leave_days_note")
    private String grantedLeaveDaysNote; // ghi chú

    @Column(name = "carried_over_leave_days")
    private Double carriedOverLeaveDays; // Số ngày nghỉ phép được chuyển từ năm trước

    @Column(name = "carried_over_leave_days_note")
    private String carriedOverLeaveDaysNote; // ghi chú

    @Column(name = "seniority_leave_days")
    private Double seniorityLeaveDays; // Số ngày nghỉ phép tăng theo thâm niên

    @Column(name = "seniority_leave_days_note")
    private String seniorityLeaveDaysNote; // ghi chú

    @Column(name = "bonus_leave_days")
    private Double bonusLeaveDays; // Số ngày nghỉ phép được thưởng khác

    @Column(name = "bonus_leave_days_note")
    private String bonusLeaveDaysNote; // ghi chú

    @Column(name = "cancelled_leave_days")
    private Double cancelledLeaveDays; // Số ngày nghỉ phép bị hủy/không được dùng

    @Column(name = "cancelled_leave_days_note")
    private String cancelledLeaveDaysNote; // ghi chú

    // Thống kê số ngày đã nghỉ theo từng tháng
    @OneToMany(mappedBy = "annualLeaveHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffMonthlyLeaveHistory> monthlyLeaveHistories;


    public Set<StaffMonthlyLeaveHistory> getMonthlyLeaveHistories() {
        return monthlyLeaveHistories;
    }

    public void setMonthlyLeaveHistories(Set<StaffMonthlyLeaveHistory> monthlyLeaveHistories) {
        this.monthlyLeaveHistories = monthlyLeaveHistories;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getGrantedLeaveDays() {
        return grantedLeaveDays;
    }

    public void setGrantedLeaveDays(Double grantedLeaveDays) {
        this.grantedLeaveDays = grantedLeaveDays;
    }

    public String getGrantedLeaveDaysNote() {
        return grantedLeaveDaysNote;
    }

    public void setGrantedLeaveDaysNote(String grantedLeaveDaysNote) {
        this.grantedLeaveDaysNote = grantedLeaveDaysNote;
    }

    public Double getCarriedOverLeaveDays() {
        return carriedOverLeaveDays;
    }

    public void setCarriedOverLeaveDays(Double carriedOverLeaveDays) {
        this.carriedOverLeaveDays = carriedOverLeaveDays;
    }

    public String getCarriedOverLeaveDaysNote() {
        return carriedOverLeaveDaysNote;
    }

    public void setCarriedOverLeaveDaysNote(String carriedOverLeaveDaysNote) {
        this.carriedOverLeaveDaysNote = carriedOverLeaveDaysNote;
    }

    public Double getSeniorityLeaveDays() {
        return seniorityLeaveDays;
    }

    public void setSeniorityLeaveDays(Double seniorityLeaveDays) {
        this.seniorityLeaveDays = seniorityLeaveDays;
    }

    public String getSeniorityLeaveDaysNote() {
        return seniorityLeaveDaysNote;
    }

    public void setSeniorityLeaveDaysNote(String seniorityLeaveDaysNote) {
        this.seniorityLeaveDaysNote = seniorityLeaveDaysNote;
    }

    public Double getBonusLeaveDays() {
        return bonusLeaveDays;
    }

    public void setBonusLeaveDays(Double bonusLeaveDays) {
        this.bonusLeaveDays = bonusLeaveDays;
    }

    public String getBonusLeaveDaysNote() {
        return bonusLeaveDaysNote;
    }

    public void setBonusLeaveDaysNote(String bonusLeaveDaysNote) {
        this.bonusLeaveDaysNote = bonusLeaveDaysNote;
    }

    public Double getCancelledLeaveDays() {
        return cancelledLeaveDays;
    }

    public void setCancelledLeaveDays(Double cancelledLeaveDays) {
        this.cancelledLeaveDays = cancelledLeaveDays;
    }

    public String getCancelledLeaveDaysNote() {
        return cancelledLeaveDaysNote;
    }

    public void setCancelledLeaveDaysNote(String cancelledLeaveDaysNote) {
        this.cancelledLeaveDaysNote = cancelledLeaveDaysNote;
    }
}
