package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.InterviewSchedule;
import com.globits.hr.domain.StaffInterviewSchedule;

import java.time.LocalDateTime;
import java.util.*;

public class CreateInterviewSchedulesDto extends BaseObjectDto {
    private List<CandidateDto> candidates; // ứng viên
    private List<UUID> candidateIds;
    private List<StaffInterviewScheduleDto> staffInterviewSchedules; // Danh sách người phỏng vấn
    private Date interviewTime; // Thời gian phỏng vấn
    private String interviewLocation; // Ghi chú
    private String note; // Ghi chú
    private Integer status; // Trạng thái: 0: chưa phỏng vấn, 1: đã phỏng vấn, 2: đã hủy
    private RecruitmentRoundDto recruitmentRound;

    public CreateInterviewSchedulesDto() {
    }

    public List<CandidateDto> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateDto> candidates) {
        this.candidates = candidates;
    }

    public List<UUID> getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(List<UUID> candidateIds) {
        this.candidateIds = candidateIds;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
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
