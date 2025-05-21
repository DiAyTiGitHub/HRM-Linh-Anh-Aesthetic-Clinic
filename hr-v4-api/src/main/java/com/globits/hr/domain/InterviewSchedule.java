package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.dto.RecruitmentRoundDto;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "tbl_interview_schedule")
public class InterviewSchedule extends BaseObject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    Candidate candidate; // ứng viên

    @OneToMany(
            mappedBy = "interviewSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<StaffInterviewSchedule> staffInterviewSchedules; // Danh sách người phỏng vấn

    @Column(name = "interview_time")
    private Date interviewTime; // Thời gian phỏng vấn

    @Column(name = "interview_localtion", columnDefinition = "TEXT")
    private String interviewLocation; // địa điẻm phỏng vấn


    @Column(name = "note", columnDefinition = "TEXT")
    private String note; // Ghi chú

    @Column(name = "status")
    private Integer status; // Trạng thái:InterviewScheduleStatus
    @ManyToOne
    @JoinColumn(name = "recruitment_round_id")
    private RecruitmentRound recruitmentRound;

    public RecruitmentRound getRecruitmentRound() {
        return recruitmentRound;
    }

    public void setRecruitmentRound(RecruitmentRound recruitmentRound) {
        this.recruitmentRound = recruitmentRound;
    }

    public InterviewSchedule() {
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public Set<StaffInterviewSchedule> getStaffInterviewSchedules() {
        return staffInterviewSchedules;
    }

    public void setStaffInterviewSchedules(Set<StaffInterviewSchedule> staffInterviewSchedules) {
        this.staffInterviewSchedules = staffInterviewSchedules;
    }

    public Date getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(Date interviewTime) {
        this.interviewTime = interviewTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
