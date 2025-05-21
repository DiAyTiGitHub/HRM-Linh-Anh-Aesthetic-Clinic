package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.template.dto.ContentTemplateDto;
import jakarta.persistence.*;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

// vòng tuyen dụng
public class RecruitmentRoundDto extends BaseObjectDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 991992518344617174L;

    private Integer roundOrder; // thứ tự vòng tuyển dụng
    private String name; // tên vòng tuyển dụng
    private RecruitmentExamTypeDto examType; // loai kiem tra
    private Date takePlaceDate; // ngay dien ra vòng tuyển dụng
    private WorkplaceDto interviewLocation; // nơi diễn ra vòng tuyển dụng
    private String description; // ghi chu = mo ta
    private RecruitmentDto recruitment; // đợt phỏng vấn
    private UUID recruitmentPlainId;
    private EvaluationTemplateDto evaluationTemplate; // Mẫu đánh gia ung vien
    private List<StaffDto> participatingPeople = new ArrayList<>();
    private List<CandidateRecruitmentRoundDto> candidates = new ArrayList<>();
    private Date actualTakePlaceDate; // ngày thực tế ứng viên được sắp xếp tham gia vòng tuyển dụng. VD: Vòng thi diễn ra vào 31/12 nhưng 2/1 ứng viên mới vào vòng tuyển => actualTakePlaceDate = 2/1
    private Integer recruitmentType; //RecruitmentType
    private Integer result; // Kết quả của ứng viên trong từng vòng tuyển dụng, Chi tiết tại: HrConstants.CandidateExamStatus
    private HrConstants.ResultStatus resultStatus; // trạng thái mỗi vòng pass hay fail
    private HrConstants.CandidateRecruitmentRoundStatus candidateRecruitmentRoundStatus;
    private ContentTemplateDto passTemplate;
    private ContentTemplateDto failTemplate;
    private UUID candidateRecruitmentRoundId;
    private RecruitmentPlanDto recruitmentPlan;
    private String errorMessage;

    public RecruitmentRoundDto() {
    }

    public RecruitmentRoundDto(RecruitmentRound entity) {
        if (entity == null) return;
        this.id = entity.getId();
        this.name = entity.getName();
        this.roundOrder = entity.getRoundOrder();
        this.description = entity.getDescription();
        this.recruitmentType = entity.getRecruitmentType();
        if (entity.getExamType() != null && entity.getExamType().getId() != null) {
            this.examType = new RecruitmentExamTypeDto();
            this.examType.setId(entity.getExamType().getId());
            this.examType.setName(entity.getExamType().getName());
            this.examType.setCode(entity.getExamType().getCode());
        }
        if (entity.getEvaluationTemplate() != null) {
            this.evaluationTemplate = new EvaluationTemplateDto(entity.getEvaluationTemplate(), false);
        }
        this.takePlaceDate = entity.getTakePlaceDate();
        this.interviewLocation = new WorkplaceDto(entity.getInterviewLocation());
        if (entity.getRecruitmentPlan() != null && entity.getRecruitmentPlan().getId() != null) {
            this.recruitmentPlainId = entity.getRecruitmentPlan().getId();
        }
        if (entity.getPassTemplate() != null) {
            this.passTemplate = new ContentTemplateDto(entity.getPassTemplate());
        }
        if (entity.getFailTemplate() != null) {
            this.failTemplate = new ContentTemplateDto(entity.getFailTemplate());
        }
        if (!CollectionUtils.isEmpty(entity.getParticipatingPeople())) {
            for (Staff staff : entity.getParticipatingPeople()) {
                StaffDto staffDto = new StaffDto(staff);
                if (entity.getJudgePerson() != null) {
                    if (staffDto.getId().equals(entity.getJudgePerson().getId())) {
                        staffDto.setJudgePerson(true);
                    }
                }
                this.participatingPeople.add(staffDto);
            }
        }
        if(!CollectionUtils.isEmpty(entity.getCandidateRecruitmentRounds())){
            this.candidates = entity.getCandidateRecruitmentRounds().stream().map(CandidateRecruitmentRoundDto::new).filter(candidateRecruitmentRoundDto -> candidateRecruitmentRoundDto.getCandidate() != null).toList();
        }
    }


    public RecruitmentRoundDto(RecruitmentRound entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

        if (entity.getRecruitment() != null) {
            this.recruitment = new RecruitmentDto(entity.getRecruitment());
        }
    }

    public HrConstants.CandidateRecruitmentRoundStatus getCandidateRecruitmentRoundStatus() {
        return candidateRecruitmentRoundStatus;
    }

    public void setCandidateRecruitmentRoundStatus(HrConstants.CandidateRecruitmentRoundStatus candidateRecruitmentRoundStatus) {
        this.candidateRecruitmentRoundStatus = candidateRecruitmentRoundStatus;
    }

    public Date getActualTakePlaceDate() {
        return actualTakePlaceDate;
    }

    public void setActualTakePlaceDate(Date actualTakePlaceDate) {
        this.actualTakePlaceDate = actualTakePlaceDate;
    }

    public Integer getRecruitmentType() {
        return recruitmentType;
    }

    public void setRecruitmentType(Integer recruitmentType) {
        this.recruitmentType = recruitmentType;
    }

    public EvaluationTemplateDto getEvaluationTemplate() {
        return evaluationTemplate;
    }

    public void setEvaluationTemplate(EvaluationTemplateDto evaluationTemplate) {
        this.evaluationTemplate = evaluationTemplate;
    }

    public Integer getRoundOrder() {
        return roundOrder;
    }

    public void setRoundOrder(Integer roundOrder) {
        this.roundOrder = roundOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecruitmentExamTypeDto getExamType() {
        return examType;
    }

    public void setExamType(RecruitmentExamTypeDto examType) {
        this.examType = examType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTakePlaceDate() {
        return takePlaceDate;
    }

    public void setTakePlaceDate(Date takePlaceDate) {
        this.takePlaceDate = takePlaceDate;
    }

    public WorkplaceDto getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(WorkplaceDto interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public RecruitmentDto getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(RecruitmentDto recruitment) {
        this.recruitment = recruitment;
    }

    public UUID getRecruitmentPlainId() {
        return recruitmentPlainId;
    }

    public void setRecruitmentPlainId(UUID recruitmentPlainId) {
        this.recruitmentPlainId = recruitmentPlainId;
    }

    public List<StaffDto> getParticipatingPeople() {
        return participatingPeople;
    }

    public void setParticipatingPeople(List<StaffDto> participatingPeople) {
        this.participatingPeople = participatingPeople;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public HrConstants.ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(HrConstants.ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public ContentTemplateDto getPassTemplate() {
        return passTemplate;
    }

    public void setPassTemplate(ContentTemplateDto passTemplate) {
        this.passTemplate = passTemplate;
    }

    public ContentTemplateDto getFailTemplate() {
        return failTemplate;
    }

    public void setFailTemplate(ContentTemplateDto failTemplate) {
        this.failTemplate = failTemplate;
    }

    public UUID getCandidateRecruitmentRoundId() {
        return candidateRecruitmentRoundId;
    }

    public void setCandidateRecruitmentRoundId(UUID candidateRecruitmentRoundId) {
        this.candidateRecruitmentRoundId = candidateRecruitmentRoundId;
    }

    public RecruitmentPlanDto getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlanDto recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<CandidateRecruitmentRoundDto> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateRecruitmentRoundDto> candidates) {
        this.candidates = candidates;
    }
}
