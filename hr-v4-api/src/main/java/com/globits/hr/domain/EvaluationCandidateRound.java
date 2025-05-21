package com.globits.hr.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

// phiếu đánh giá ứng viên qua từng vòng
@Entity
@Table(name = "tbl_evaluation_candidate_round")
public class EvaluationCandidateRound extends BaseObject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_recruitment_round_id")
    private CandidateRecruitmentRound candidateRecruitmentRound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private EvaluationTemplate template;

    @OneToMany(mappedBy = "evaluationCandidateRound", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluationTemplateItemValue> evaluationValues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private Staff interviewer;

    private String note;

    // 1. Kết quả tuyển dụng
    @Column(name = "result")
    private Integer result; //InterviewResultType

    @Column(name = "candidate_job_title")
    private String candidateJobTitle;

    @Column(name = "interviewer_job_title")
    private String interviewerJobTitle;

    @Column(name = "candidate_expected_salary")
    private Double candidateExpectedSalary;

    @Column(name = "interviewer_expected_salary")
    private Double interviewerExpectedSalary;

    @Column(name = "candidate_start_working_date")
    private Date candidateStartWorkingDate;

    @Column(name = "interviewer_start_working_date")
    private Date interviewerStartWorkingDate;

    public EvaluationCandidateRound() {
    }

// Getter và Setter


    public EvaluationTemplate getTemplate() {
        return template;
    }

    public void setTemplate(EvaluationTemplate template) {
        this.template = template;
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

    public CandidateRecruitmentRound getCandidateRecruitmentRound() {
        return candidateRecruitmentRound;
    }

    public void setCandidateRecruitmentRound(CandidateRecruitmentRound candidateRecruitmentRound) {
        this.candidateRecruitmentRound = candidateRecruitmentRound;
    }

    public Set<EvaluationTemplateItemValue> getEvaluationValues() {
        return evaluationValues;
    }

    public void setEvaluationValues(Set<EvaluationTemplateItemValue> evaluationValues) {
        this.evaluationValues = evaluationValues;
    }

    public Staff getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(Staff interviewer) {
        this.interviewer = interviewer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
