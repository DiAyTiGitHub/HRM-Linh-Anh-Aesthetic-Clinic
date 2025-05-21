package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.OrganizationDto;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.CandidateRecruitmentRound;
import com.globits.hr.domain.CandidateRecruitmentRoundDocument;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class CandidateRecruitmentRoundDto extends BaseObjectDto {
    private CandidateDto candidate;
    private RecruitmentRoundDto recruitmentRound;
    private Integer recruitmentType; //RecruitmentType loại phỏng vấn online hay offline
    private String note; // nhận xét ứng viên
    private Integer result; // Kết quả của ứng viên trong từng vòng tuyển dụng, Chi tiết tại: HrConstants.CandidateExamStatus
    private Date actualTakePlaceDate; // ngày thực tế ứng viên được sắp xếp tham gia vòng tuyển dụng. VD: Vòng thi diễn ra vào 31/12 nhưng 2/1 ứng viên mới vào vòng tuyển => actualTakePlaceDate = 2/1
    //    private String examPosition; // vị trí dự thi/ phỏng vấn
    private WorkplaceDto workplace;// vị trí dự thi/ phỏng vấn
    private HrConstants.CandidateRecruitmentRoundStatus status; // Trạng thái tham gia hay ko tham gia
    private HrConstants.ResultStatus resultStatus; // trạng thái mỗi vòng pass hay fail
    private Set<CandidateRecruitmentRoundDocumentDto> documents = new HashSet<CandidateRecruitmentRoundDocumentDto>();// Quá trình chức vụ
    private Set<EvaluationCandidateRoundDto> evaluationCandidateRoundDtos = new HashSet<EvaluationCandidateRoundDto>();// Quá trình chức vụ
    private boolean current = false;

    public CandidateRecruitmentRoundDto() {
    }

    public CandidateRecruitmentRoundDto(CandidateRecruitmentRound entity) {
        super(entity);

        if (entity == null) return;

        this.note = entity.getNote();
        this.actualTakePlaceDate = entity.getActualTakePlaceDate();
        this.status = entity.getStatus();
        this.recruitmentType = entity.getRecruitmentType();

        if (entity.getWorkplace() != null) {
            this.workplace = new WorkplaceDto(entity.getWorkplace());
        }
        if (entity.getCandidate() != null && (entity.getCandidate().getVoided() == null || entity.getCandidate().getVoided() == false)) {
            this.candidate = new CandidateDto();
            this.candidate.setId(entity.getCandidate().getId());
            this.candidate.setCandidateCode(entity.getCandidate().getCandidateCode());
            this.candidate.setDisplayName(entity.getCandidate().getDisplayName());
            this.candidate.setBirthDate(entity.getCandidate().getBirthDate());
            this.candidate.setGender(entity.getCandidate().getGender());
            this.candidate.setPhoneNumber(entity.getCandidate().getPhoneNumber());
            this.candidate.setEmail(entity.getCandidate().getEmail());
            this.candidate.setSubmissionDate(entity.getCandidate().getSubmissionDate());
            this.candidate.setStatus(entity.getCandidate().getStatus());

            if (entity.getCandidate().getOrganization() != null) {
                HrOrganizationDto organization = new HrOrganizationDto();
                organization.setId(entity.getCandidate().getOrganization().getId());
                organization.setName(entity.getCandidate().getOrganization().getName());
                this.candidate.setOrganization(organization);
            }

            if (entity.getCandidate().getDepartment() != null) {
                HRDepartmentDto department = new HRDepartmentDto();
                department.setId(entity.getCandidate().getDepartment().getId());
                department.setName(entity.getCandidate().getDepartment().getName());
                this.candidate.setDepartment(department);
            }

            if (entity.getCandidate().getPositionTitle() != null) {
                PositionTitleDto positionTitle = new PositionTitleDto();
                positionTitle.setId(entity.getCandidate().getPositionTitle().getId());
                positionTitle.setName(entity.getCandidate().getPositionTitle().getName());
                this.candidate.setPositionTitle(positionTitle);
            }
        }

        if (entity.getRecruitmentRound() != null) {
            this.recruitmentRound = new RecruitmentRoundDto();

            this.recruitmentRound.setId(entity.getRecruitmentRound().getId());
            this.recruitmentRound.setName(entity.getRecruitmentRound().getName());
            this.recruitmentRound.setRoundOrder(entity.getRecruitmentRound().getRoundOrder());
            this.recruitmentRound.setExamType(new RecruitmentExamTypeDto(entity.getRecruitmentRound().getExamType()));
        }

        this.status = entity.getStatus();
        this.resultStatus = entity.getResultStatus();

        if (entity.getDocuments() != null && !entity.getDocuments().isEmpty()) {
            this.documents = new HashSet<>();
            for (CandidateRecruitmentRoundDocument document : entity.getDocuments()) {
                CandidateRecruitmentRoundDocumentDto dto = new CandidateRecruitmentRoundDocumentDto(document);
                this.documents.add(dto);
            }
        }
        if (!CollectionUtils.isEmpty(entity.getEvaluationTicket())) {
            this.evaluationCandidateRoundDtos = new HashSet<>();
            this.evaluationCandidateRoundDtos.addAll(entity.getEvaluationTicket().stream().map(EvaluationCandidateRoundDto::new).collect(java.util.stream.Collectors.toSet()));
        }
    }

    public CandidateRecruitmentRoundDto(CandidateRecruitmentRound entity, Boolean isDetail) {
        this(entity);

        if (isDetail != null && isDetail.equals(true)) {

        }
    }

    public Integer getRecruitmentType() {
        return recruitmentType;
    }

    public void setRecruitmentType(Integer recruitmentType) {
        this.recruitmentType = recruitmentType;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public Set<CandidateRecruitmentRoundDocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<CandidateRecruitmentRoundDocumentDto> documents) {
        this.documents = documents;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
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

    public HrConstants.CandidateRecruitmentRoundStatus getStatus() {
        return status;
    }

    public void setStatus(HrConstants.CandidateRecruitmentRoundStatus status) {
        this.status = status;
    }

    public HrConstants.ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(HrConstants.ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public Set<EvaluationCandidateRoundDto> getEvaluationCandidateRoundDtos() {
        return evaluationCandidateRoundDtos;
    }

    public void setEvaluationCandidateRoundDtos(Set<EvaluationCandidateRoundDto> evaluationCandidateRoundDtos) {
        this.evaluationCandidateRoundDtos = evaluationCandidateRoundDtos;
    }
}
