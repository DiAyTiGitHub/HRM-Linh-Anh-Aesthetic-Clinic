package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.dto.EvaluationTemplateDto;
import com.globits.task.domain.HrTaskLabel;
import com.globits.template.domain.ContentTemplate;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

// vòng tuyển dụng
@Table(name = "tbl_recruitment_round")
@Entity
public class RecruitmentRound extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    private Integer roundOrder; // thứ tự vòng tuyển dụng

    private String name; // tên vòng tuyển dụng

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_type_id")
    private RecruitmentExamType examType; // loai kiem tra

    private Date takePlaceDate; // ngay dien ra vòng tuyển dụng
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "work_place_id")
    private Workplace interviewLocation; // nơi diễn ra vòng tuyển dụng

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description; // ghi chu = mo ta

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment; // đợt phỏng vấn

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_plan_id")
    private RecruitmentPlan recruitmentPlan; // đợt phỏng vấn

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_template_id")
    private EvaluationTemplate evaluationTemplate; // Mẫu đánh gia ung vien

    @OneToMany(mappedBy = "recruitmentRound", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<CandidateRecruitmentRound> candidateRecruitmentRounds;

    @Column(name = "recruitment_type")
    private Integer recruitmentType; //RecruitmentType

    @ManyToMany
    @JoinTable(
            name = "tbl_recruitment_round_staff",
            joinColumns = @JoinColumn(name = "recruitment_round_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    private Set<Staff> participatingPeople = new HashSet<>(); // người tham gia vòng phỏng vấn

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_person")
    private Staff judgePerson; // người phụ trách vòng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pass_template_id")
    private ContentTemplate passTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fail_template_id")
    private ContentTemplate failTemplate;

    public Integer getRecruitmentType() {
        return recruitmentType;
    }

    public void setRecruitmentType(Integer recruitmentType) {
        this.recruitmentType = recruitmentType;
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

    public RecruitmentExamType getExamType() {
        return examType;
    }

    public void setExamType(RecruitmentExamType examType) {
        this.examType = examType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Recruitment getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public Date getTakePlaceDate() {
        return takePlaceDate;
    }

    public void setTakePlaceDate(Date takePlaceDate) {
        this.takePlaceDate = takePlaceDate;
    }

    public Workplace getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(Workplace interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public Set<CandidateRecruitmentRound> getCandidateRecruitmentRounds() {
        return candidateRecruitmentRounds;
    }

    public void setCandidateRecruitmentRounds(Set<CandidateRecruitmentRound> candidateRecruitmentRounds) {
        this.candidateRecruitmentRounds = candidateRecruitmentRounds;
    }

    public EvaluationTemplate getEvaluationTemplate() {
        return evaluationTemplate;
    }

    public void setEvaluationTemplate(EvaluationTemplate evaluationTemplate) {
        this.evaluationTemplate = evaluationTemplate;
    }

    public RecruitmentPlan getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlan recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
    }

    public Set<Staff> getParticipatingPeople() {
        return participatingPeople;
    }

    public void setParticipatingPeople(Set<Staff> participatingPeople) {
        this.participatingPeople = participatingPeople;
    }

    public Staff getJudgePerson() {
        return judgePerson;
    }

    public void setJudgePerson(Staff judgePerson) {
        this.judgePerson = judgePerson;
    }

    public ContentTemplate getPassTemplate() {
        return passTemplate;
    }

    public void setPassTemplate(ContentTemplate passTemplate) {
        this.passTemplate = passTemplate;
    }

    public ContentTemplate getFailTemplate() {
        return failTemplate;
    }

    public void setFailTemplate(ContentTemplate failTemplate) {
        this.failTemplate = failTemplate;
    }
}

