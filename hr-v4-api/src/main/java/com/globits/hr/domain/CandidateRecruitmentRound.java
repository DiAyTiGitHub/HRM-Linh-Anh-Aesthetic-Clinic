package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.HrConstants;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_candidate_recruitment_round",
        uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_id", "recruitment_round_id"})
)
public class CandidateRecruitmentRound extends BaseObject {

    @Column(name = "note")
    private String note; // nhận xét ứng viên

    @Column(name = "actual_take_place_date")
    private Date actualTakePlaceDate; // ngày thực tế ứng viên được sắp xếp tham gia vòng tuyển dụng. VD: Vòng thi diễn ra vào 31/12 nhưng 2/1 ứng viên mới vào vòng tuyển => actualTakePlaceDate = 2/1

    //    @Column(name = "exam_position")
//    private String examPosition; // vị trí ngồi dự thi/phỏng vấn
    @ManyToOne
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;// Địa điểm phỏng vấn

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "recruitment_round_id", nullable = false)
    private RecruitmentRound recruitmentRound;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private HrConstants.CandidateRecruitmentRoundStatus status; // Trạng thái tham gia hay ko tham gia

    @Column(name = "result_status")
    @Enumerated(value = EnumType.STRING)
    private HrConstants.ResultStatus resultStatus; // trạng thái mỗi vòng pass hay fail

    @Column(name = "result")
    private Integer result; // Kết quả của ứng viên trong từng vòng tuyển dụng, Chi tiết tại: HrConstants.CandidateExamStatus

    @Column(name = "recruitment_type")
    private Integer recruitmentType; //RecruitmentType loại phỏng vấn online hay offline

    @OneToMany(mappedBy = "round", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CandidateRecruitmentRoundDocument> documents = new HashSet<CandidateRecruitmentRoundDocument>();// Quá trình chức vụ

    @Column(name = "is_send_mail")
    private Boolean isSendMail; // đã gửi mail hay chưa

    @OneToMany(mappedBy = "candidateRecruitmentRound", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluationCandidateRound> evaluationTicket = new HashSet<EvaluationCandidateRound>();// Quá trình chức vụ

    public Integer getRecruitmentType() {
        return recruitmentType;
    }

    public void setRecruitmentType(Integer recruitmentType) {
        this.recruitmentType = recruitmentType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public RecruitmentRound getRecruitmentRound() {
        return recruitmentRound;
    }

    public void setRecruitmentRound(RecruitmentRound recruitmentRound) {
        this.recruitmentRound = recruitmentRound;
    }

    public Date getActualTakePlaceDate() {
        return actualTakePlaceDate;
    }

    public void setActualTakePlaceDate(Date actualTakePlaceDate) {
        this.actualTakePlaceDate = actualTakePlaceDate;
    }

    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }

    public HrConstants.CandidateRecruitmentRoundStatus getStatus() {
        return status;
    }

    public Set<CandidateRecruitmentRoundDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<CandidateRecruitmentRoundDocument> documents) {
        this.documents = documents;
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

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Boolean getSendMail() {
        return isSendMail;
    }

    public void setSendMail(Boolean sendMail) {
        isSendMail = sendMail;
    }

    public Set<EvaluationCandidateRound> getEvaluationTicket() {
        return evaluationTicket;
    }

    public void setEvaluationTicket(Set<EvaluationCandidateRound> evaluationTicket) {
        this.evaluationTicket = evaluationTicket;
    }
}
