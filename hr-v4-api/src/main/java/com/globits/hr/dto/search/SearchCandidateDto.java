package com.globits.hr.dto.search;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.hr.HrConstants;
import com.globits.hr.dto.*;

public class SearchCandidateDto extends SearchDto {
    private UUID recruitmentPlanId;
    private Integer status;
    private Integer approvalStatus; // trang thai ho so ung vien HrConstants.CandidateApprovalStatus
    // Xem status: HrConstants.CandidateApprovalStatus
    private Integer examStatus; // trang thai ung vien co PASS bai test cua dot phong van/thi tuyen hay khong
    // Xem status: HrConstants.CandidateExamStatus
    private Integer receptionStatus; // trạng thái của ứng viên sau khi đã PASS bài phỏng vấn/thi tuyển, trạng thái
    // này chỉ ứng viên có được nhận việc hay không
    // Xem status: HrConstants.CandidateReceptionStatus
    private Integer onboardStatus; // trạng thái chỉ tình trạng nhận việc của ứng viên (không đến nhận việc, đã
    // nhận việc,...)
    // Xem status: HrConstants.CandidateOnboardStatus
    private List<UUID> candidateIds;
    private UUID candidateId; // dung cho cac chuc nang chi cho chon 1 ung vien/1 thao tac
    private String refusalReason; // ly do tu choi

    private Date submissionDate; // Ngày ứng viên nop ho so
    private Date onboardDate; // Ngày tiếp nhận ứng viên
    private Date interviewDate; // Ngày ứng viên làm bài phỏng vấn/ thi tuyển

    private RecruitmentRequestDto recruitmentRequest; // yêu cầu tuyển dụng
    private RecruitmentPlanDto recruitmentPlan; // kế hoạch tuyển dụng
    private RecruitmentDto recruitment; // lọc theo đợt tuyển dụng
    private UUID recruitmentId; // lọc theo đợt tuyển dụng
    private PositionTitleDto positionTitle; // lọc theo vị trí ứng tuyển

    private Date submissionDateFrom; // ngày nộp hồ sơ TỪ
    private Date submissionDateTo; // ngày nộp hồ sơ ĐẾN
    private List<UUID> recruitmentRoundIds;
    private Date interviewDateFrom; // ngày phỏng vấn/thi tuyển TỪ
    private Date interviewDateTo; // ngày phỏng vấn/thi tuyển ĐẾN

    private Date onboardDateFrom; // ngày nhận việc TỪ
    private Date onboardDateTo; // ngày nhận việc ĐÊN
    private Boolean findNullPlain = false;
    private HrConstants.CandidateRecruitmentRoundStatus recruitmentRoundStatus;

    public HrConstants.CandidateRecruitmentRoundStatus getRecruitmentRoundStatus() {
        return recruitmentRoundStatus;
    }

    public void setRecruitmentRoundStatus(HrConstants.CandidateRecruitmentRoundStatus recruitmentRoundStatus) {
        this.recruitmentRoundStatus = recruitmentRoundStatus;
    }

    public List<UUID> getRecruitmentRoundIds() {
        return recruitmentRoundIds;
    }

    public void setRecruitmentRoundIds(List<UUID> recruitmentRoundIds) {
        this.recruitmentRoundIds = recruitmentRoundIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UUID getRecruitmentPlanId() {
        return recruitmentPlanId;
    }

    public void setRecruitmentPlanId(UUID recruitmentPlanId) {
        this.recruitmentPlanId = recruitmentPlanId;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(Integer examStatus) {
        this.examStatus = examStatus;
    }

    public Integer getReceptionStatus() {
        return receptionStatus;
    }

    public void setReceptionStatus(Integer receptionStatus) {
        this.receptionStatus = receptionStatus;
    }

    public List<UUID> getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(List<UUID> candidateIds) {
        this.candidateIds = candidateIds;
    }

    public String getRefusalReason() {
        return refusalReason;
    }

    public void setRefusalReason(String refusalReason) {
        this.refusalReason = refusalReason;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public Date getOnboardDate() {
        return onboardDate;
    }

    public void setOnboardDate(Date onboardDate) {
        this.onboardDate = onboardDate;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    public RecruitmentDto getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(RecruitmentDto recruitment) {
        this.recruitment = recruitment;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Date getSubmissionDateFrom() {
        return submissionDateFrom;
    }

    public void setSubmissionDateFrom(Date submissionDateFrom) {
        this.submissionDateFrom = submissionDateFrom;
    }

    public Date getSubmissionDateTo() {
        return submissionDateTo;
    }

    public void setSubmissionDateTo(Date submissionDateTo) {
        this.submissionDateTo = submissionDateTo;
    }

    public Date getInterviewDateFrom() {
        return interviewDateFrom;
    }

    public void setInterviewDateFrom(Date interviewDateFrom) {
        this.interviewDateFrom = interviewDateFrom;
    }

    public Date getInterviewDateTo() {
        return interviewDateTo;
    }

    public void setInterviewDateTo(Date interviewDateTo) {
        this.interviewDateTo = interviewDateTo;
    }

    public Integer getOnboardStatus() {
        return onboardStatus;
    }

    public void setOnboardStatus(Integer onboardStatus) {
        this.onboardStatus = onboardStatus;
    }

    public RecruitmentRequestDto getRecruitmentRequest() {
        return recruitmentRequest;
    }

    public void setRecruitmentRequest(RecruitmentRequestDto recruitmentRequest) {
        this.recruitmentRequest = recruitmentRequest;
    }

    public RecruitmentPlanDto getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlanDto recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
    }

    public Date getOnboardDateFrom() {
        return onboardDateFrom;
    }

    public void setOnboardDateFrom(Date onboardDateFrom) {
        this.onboardDateFrom = onboardDateFrom;
    }

    public Date getOnboardDateTo() {
        return onboardDateTo;
    }

    public void setOnboardDateTo(Date onboardDateTo) {
        this.onboardDateTo = onboardDateTo;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public UUID getRecruitmentId() {
        return recruitmentId;
    }

    public void setRecruitmentId(UUID recruitmentId) {
        this.recruitmentId = recruitmentId;
    }

    public Boolean getFindNullPlain() {
        return findNullPlain;
    }

    public void setFindNullPlain(Boolean findNullPlain) {
        this.findNullPlain = findNullPlain;
    }
}
