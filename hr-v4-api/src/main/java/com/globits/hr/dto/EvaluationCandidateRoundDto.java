package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.EvaluationCandidateRound;

import java.util.*;
import java.util.stream.Collectors;

// phiếu đánh giá ứng viên qua từng vòng
public class EvaluationCandidateRoundDto extends BaseObjectDto {

    private CandidateRecruitmentRoundDto candidateRecruitmentRound;

    private Set<EvaluationTemplateItemValueDto> evaluationValues;

    private StaffDto interviewer;

    private String note;

    // 1. Kết quả tuyển dụng
    private Integer result; //InterviewResultType

    private String candidateJobTitle;

    private String interviewerJobTitle;

    private Double candidateExpectedSalary;

    private Double interviewerExpectedSalary;

    private Date candidateStartWorkingDate;

    private Date interviewerStartWorkingDate;


    private List<EvaluationDto> evaluations;

    public EvaluationCandidateRoundDto() {
    }

    public EvaluationCandidateRoundDto(EvaluationCandidateRound entity) {
        super(entity);
        this.note = entity.getNote();
        this.result = entity.getResult();
        this.candidateJobTitle = entity.getCandidateJobTitle();
        this.interviewerJobTitle = entity.getInterviewerJobTitle();
        this.candidateExpectedSalary = entity.getCandidateExpectedSalary();
        this.interviewerExpectedSalary = entity.getInterviewerExpectedSalary();
        this.candidateStartWorkingDate = entity.getCandidateStartWorkingDate();
        this.interviewerStartWorkingDate = entity.getInterviewerStartWorkingDate();

        if (entity.getInterviewer() != null) {
            this.interviewer = new StaffDto(entity.getInterviewer(), false, false);
        }
        if (entity.getCandidateRecruitmentRound() != null) {
            this.candidateRecruitmentRound = new CandidateRecruitmentRoundDto();
            this.candidateRecruitmentRound.setId(entity.getCandidateRecruitmentRound().getId());
        }


    }

    public EvaluationCandidateRoundDto(EvaluationCandidateRound entity, Boolean viewAll) {
        this(entity);

        if (viewAll) {
            if (entity.getEvaluationValues() != null && !entity.getEvaluationValues().isEmpty()) {
                this.evaluationValues = entity.getEvaluationValues()
                        .stream()
                        .filter(item -> item.getParent() == null) // hoặc !item.getParent() nếu kiểu Boolean
                        .map(item -> new EvaluationTemplateItemValueDto(item, false))
                        .sorted(Comparator
                                .comparing((EvaluationTemplateItemValueDto dto) -> dto.getParentItemId() == null ? 0 : 1) // parentItemId == null trước
                                .thenComparingInt(dto -> dto.getNumberSortItem() != null ? dto.getNumberSortItem() : Integer.MAX_VALUE) // số nhỏ xếp trước
                        )
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                this.evaluations = entity.getEvaluationValues().stream()
                        .map(e -> new EvaluationDto(e.getId(), e.getEvaluationTemplateItem().getId(), e.getValue()))
                        .collect(Collectors.toList());

            }
        }
    }

// Getter và Setter

    public List<EvaluationDto> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<EvaluationDto> evaluations) {
        this.evaluations = evaluations;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getCandidateJobTitle() {
        return candidateJobTitle;
    }

    public void setCandidateJobTitle(String candidateJobTitle) {
        this.candidateJobTitle = candidateJobTitle;
    }

    public String getInterviewerJobTitle() {
        return interviewerJobTitle;
    }

    public void setInterviewerJobTitle(String interviewerJobTitle) {
        this.interviewerJobTitle = interviewerJobTitle;
    }

    public Double getCandidateExpectedSalary() {
        return candidateExpectedSalary;
    }

    public void setCandidateExpectedSalary(Double candidateExpectedSalary) {
        this.candidateExpectedSalary = candidateExpectedSalary;
    }

    public Double getInterviewerExpectedSalary() {
        return interviewerExpectedSalary;
    }

    public void setInterviewerExpectedSalary(Double interviewerExpectedSalary) {
        this.interviewerExpectedSalary = interviewerExpectedSalary;
    }

    public Date getCandidateStartWorkingDate() {
        return candidateStartWorkingDate;
    }

    public void setCandidateStartWorkingDate(Date candidateStartWorkingDate) {
        this.candidateStartWorkingDate = candidateStartWorkingDate;
    }

    public Date getInterviewerStartWorkingDate() {
        return interviewerStartWorkingDate;
    }

    public void setInterviewerStartWorkingDate(Date interviewerStartWorkingDate) {
        this.interviewerStartWorkingDate = interviewerStartWorkingDate;
    }

    public CandidateRecruitmentRoundDto getCandidateRecruitmentRound() {
        return candidateRecruitmentRound;
    }

    public void setCandidateRecruitmentRound(CandidateRecruitmentRoundDto candidateRecruitmentRound) {
        this.candidateRecruitmentRound = candidateRecruitmentRound;
    }

    public Set<EvaluationTemplateItemValueDto> getEvaluationValues() {
        return evaluationValues;
    }

    public void setEvaluationValues(Set<EvaluationTemplateItemValueDto> evaluationValues) {
        this.evaluationValues = evaluationValues;
    }

    public StaffDto getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(StaffDto interviewer) {
        this.interviewer = interviewer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
