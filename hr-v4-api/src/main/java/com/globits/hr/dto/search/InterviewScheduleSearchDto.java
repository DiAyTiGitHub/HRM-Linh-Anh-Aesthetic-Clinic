package com.globits.hr.dto.search;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class InterviewScheduleSearchDto extends SearchDto {
    private UUID candidateId;
    private Date fromDate;
    private Date toDate;
    private Integer status;
    private UUID recruitmentRoundId;
    private List<UUID> staffInterviewSchedules;

    public InterviewScheduleSearchDto() {
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    @Override
    public Date getFromDate() {
        return fromDate;
    }

    @Override
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @Override
    public Date getToDate() {
        return toDate;
    }

    @Override
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UUID getRecruitmentRoundId() {
        return recruitmentRoundId;
    }

    public void setRecruitmentRoundId(UUID recruitmentRoundId) {
        this.recruitmentRoundId = recruitmentRoundId;
    }

    public List<UUID> getStaffInterviewSchedules() {
        return staffInterviewSchedules;
    }

    public void setStaffInterviewSchedules(List<UUID> staffInterviewSchedules) {
        this.staffInterviewSchedules = staffInterviewSchedules;
    }
}
