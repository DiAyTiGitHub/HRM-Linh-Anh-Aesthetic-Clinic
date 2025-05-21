package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.InterviewSchedule;
import com.globits.hr.domain.StaffInterviewSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InterviewScheduleDto extends BaseObjectDto {
    private CandidateDto candidate; // ứng viên
    private List<StaffInterviewScheduleDto> staffInterviewSchedules; // Danh sách người phỏng vấn
    private Date interviewTime; // Thời gian phỏng vấn
    private String interviewLocation; // Ghi chú
    private String note; // Ghi chú
    private Integer status; // Trạng thái: 0: chưa phỏng vấn, 1: đã phỏng vấn, 2: đã hủy
    private RecruitmentRoundDto recruitmentRound;

    public InterviewScheduleDto() {
    }

    public InterviewScheduleDto(InterviewSchedule entity) {
        super(entity);
        if (entity.getCandidate() != null) {
            this.candidate = new CandidateDto(entity.getCandidate());
        }
        if (entity.getStaffInterviewSchedules() != null) {
            staffInterviewSchedules = new ArrayList<>();
            for (StaffInterviewSchedule staffInterviewSchedule : entity.getStaffInterviewSchedules()) {
                staffInterviewSchedules.add(new StaffInterviewScheduleDto(staffInterviewSchedule));
            }
        }
        this.interviewTime = entity.getInterviewTime();
        this.interviewLocation = entity.getInterviewLocation();
        this.note = entity.getNote();
        this.status = entity.getStatus();
        if (entity.getRecruitmentRound() != null) {
            this.recruitmentRound = new RecruitmentRoundDto(entity.getRecruitmentRound(), false);
        }
    }

    public CandidateDto getCandidate() {
        return candidate;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public void setCandidate(CandidateDto candidate) {
        this.candidate = candidate;
    }

    public List<StaffInterviewScheduleDto> getStaffInterviewSchedules() {
        return staffInterviewSchedules;
    }

    public void setStaffInterviewSchedules(List<StaffInterviewScheduleDto> staffInterviewSchedules) {
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

    public RecruitmentRoundDto getRecruitmentRound() {
        return recruitmentRound;
    }

    public void setRecruitmentRound(RecruitmentRoundDto recruitmentRound) {
        this.recruitmentRound = recruitmentRound;
    }
}
