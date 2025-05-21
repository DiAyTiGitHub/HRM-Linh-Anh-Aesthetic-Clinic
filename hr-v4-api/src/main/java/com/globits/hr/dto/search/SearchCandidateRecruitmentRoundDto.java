package com.globits.hr.dto.search;

import com.globits.core.dto.SearchDto;
import com.globits.hr.domain.Workplace;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.RecruitmentRoundDto;
import com.globits.hr.dto.WorkplaceDto;
import jakarta.persistence.Column;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SearchCandidateRecruitmentRoundDto extends SearchDto {
    private CandidateDto candidate;
    private RecruitmentRoundDto recruitmentRound;
    private UUID recruitmentRoundId;

    private Set<UUID> recruitmentRoundIds;

    private List<UUID> chosenRecordIds;

    // Kết quả của ứng viên trong từng vòng tuyển dụng, Chi tiết tại: HrConstants.CandidateExamStatus
    private Integer result;

    // nhận xét ứng viên
    private String note;

    // ngày thực tế ứng viên được sắp xếp tham gia vòng tuyển dụng. VD: Vòng thi diễn ra vào 31/12 nhưng 2/1 ứng viên mới vào vòng tuyển => actualTakePlaceDate = 2/1
    private Date actualTakePlaceDate;

    // vị trí ngồi dự thi/phỏng vấn
    private WorkplaceDto workplace;
    private UUID workplaceId;

    public Set<UUID> getRecruitmentRoundIds() {
        return recruitmentRoundIds;
    }

    public void setRecruitmentRoundIds(Set<UUID> recruitmentRoundIds) {
        this.recruitmentRoundIds = recruitmentRoundIds;
    }

    public CandidateDto getCandidate() {
        return candidate;
    }

    public void setCandidate(CandidateDto candidate) {
        this.candidate = candidate;
    }

    public RecruitmentRoundDto getRecruitmentRound() {
        return recruitmentRound;
    }

    public void setRecruitmentRound(RecruitmentRoundDto recruitmentRound) {
        this.recruitmentRound = recruitmentRound;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public List<UUID> getChosenRecordIds() {
        return chosenRecordIds;
    }

    public void setChosenRecordIds(List<UUID> chosenRecordIds) {
        this.chosenRecordIds = chosenRecordIds;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getActualTakePlaceDate() {
        return actualTakePlaceDate;
    }

    public void setActualTakePlaceDate(Date actualTakePlaceDate) {
        this.actualTakePlaceDate = actualTakePlaceDate;
    }

    public WorkplaceDto getWorkplace() {
        return workplace;
    }

    public void setWorkplace(WorkplaceDto workplace) {
        this.workplace = workplace;
    }

    public UUID getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(UUID workplaceId) {
        this.workplaceId = workplaceId;
    }

    public UUID getRecruitmentRoundId() {
        return recruitmentRoundId;
    }

    public void setRecruitmentRoundId(UUID recruitmentRoundId) {
        this.recruitmentRoundId = recruitmentRoundId;
    }
}
