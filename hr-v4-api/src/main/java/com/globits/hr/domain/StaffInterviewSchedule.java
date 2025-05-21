package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_staff_interview_schedule")
public class StaffInterviewSchedule extends BaseObject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private Staff interviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_schedule_id")
    private InterviewSchedule interviewSchedule;

    private Integer status;
    private String interviewRole;
    private String note;

    public StaffInterviewSchedule() {
    }

    public String getInterviewRole() {
        return interviewRole;
    }

    public void setInterviewRole(String interviewRole) {
        this.interviewRole = interviewRole;
    }

    public Staff getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(Staff interviewer) {
        this.interviewer = interviewer;
    }

    public InterviewSchedule getInterviewSchedule() {
        return interviewSchedule;
    }

    public void setInterviewSchedule(InterviewSchedule interviewSchedule) {
        this.interviewSchedule = interviewSchedule;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
