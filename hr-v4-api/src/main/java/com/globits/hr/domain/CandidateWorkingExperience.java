package com.globits.hr.domain;

import java.util.Date;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Kinh nghiệm làm việc của ứng viên ở các công ty/ tổ chức cũ
@Entity
@Table(name = "tbl_candidate_working_experience")
public class CandidateWorkingExperience extends BaseObject {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    private String companyName;

    // ngày bắt đầu làm việc
    @Column(name = "start_date")
    private Date startDate;

    // ngày kết thúc làm việc
    @Column(name = "end_date")
    private Date endDate;

    // vị trí làm việc
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;

    // Mức lương trước khi nghỉ việc
    private Double salary;

    // Lý do nghỉ việc
    @Column(name = "leaving_reason", columnDefinition = "TEXT")
    private String leavingReason;

    // Mô tả công việc
    @Column(name = "decription", columnDefinition = "TEXT")
    private String decription;
    @Column(name = "old_position")
    protected String oldPosition;

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getLeavingReason() {
        return leavingReason;
    }

    public void setLeavingReason(String leavingReason) {
        this.leavingReason = leavingReason;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public String getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(String oldPosition) {
        this.oldPosition = oldPosition;
    }
}
