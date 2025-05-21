package com.globits.hr.domain;

import com.globits.core.domain.*;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/*
 * ứng viên
 */
@Entity
@Table(name = "tbl_candidate")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "candidate_person", value = ConstraintMode.NO_CONSTRAINT))
@DiscriminatorValue("2")
public class Candidate extends Person {
    private static final long serialVersionUID = 6014783475303579207L;

    // tab 1 - thong tin ung vien
    @Column(name = "candidate_code", nullable = true, unique = true)
    private String candidateCode; // ma ung vien

    @ManyToOne
    @JoinColumn(name = "administrative_unit_id")
    private AdministrativeUnit administrativeUnit; // Xa phuong, tinh thanh, que quan
    private String permanentResidence; // Hộ khẩu thường trú
    private String currentResidence; // Noi o hien tai

    // tab 2 - thong tin tuyen dung
    @ManyToOne
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment; // dot tuyen dung

    @ManyToOne
    @JoinColumn(name = "recruitment_plan_id")
    private RecruitmentPlan recruitmentPlan;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private HrOrganization organization; // Đơn vị ứng tuyển

    @ManyToOne
    @JoinColumn(name = "department_id")
    private HRDepartment department; // Phòng ban ứng tuyển (được chọn các phòng ban trong đơn vị ứng tuyển)

    @ManyToOne
    @JoinColumn(name = "position_title_id")
    private PositionTitle positionTitle; // Vị trí ứng tuyển (được chọn các vị trí trong phòng ban ứng tuyển)

    private Date submissionDate; // Ngày nop ho so
    private Date interviewDate; // Ngày phong van = Ngày gap mat ứng viên
    private Double desiredPay; // muc luong mong muon
    private Date possibleWorkingDate; // Ngày co the lam việc
    private Date onboardDate; // Ngày ứng viên nhận việc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introducer_id")
    private Staff introducer; // Nhân viên giới thiệu ứng viên

    // tab 3 - quá trình đào tạo
    @OneToMany(mappedBy = "candidate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CandidateEducationHistory> candidateEducationHistory; // Quá trình đào tạo của ứng viên

    // tab 4 - Chứng chỉ hiện có của ứng viên
    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PersonCertificate> candidateCertificates;// Trinh do hoc van / chung chi cua ung vien

    // tab 5 - Kinh nghiệm làm việc
    @OneToMany(mappedBy = "candidate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CandidateWorkingExperience> candidateWorkingExperience; // Kinh nghiệm làm việc

    // tab 6 - Tệp đính kèm
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<CandidateAttachment> candidateAttachments = new HashSet<CandidateAttachment>(); // tệp đính kèm

    // Các trường khác
    // map to staff
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
//    @JoinColumn(name = "staff_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_staff_candidate"))
    private Staff staff;

    //Thông tin các vòng tuyển dụng của ứng viên
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<CandidateRecruitmentRound> candidateRecruitmentRounds;

    private Integer status; // trạng thái của ứng viên ( Gộp hết status bên dưới vào đây) Xem status: HrConstants.CandidateStatus
    private Integer preScreenStatus; //PreScreenStatus trạng thái sơ lọc
    private Integer onboardStatus; // trạng thái chỉ tình trạng nhận việc của ứng viên (không đến nhận việc, đã nhận việc,...)// Xem status: HrConstants.CandidateReceptionStatus


    // Xem status: HrConstants.CandidateOnboardStatus
    // list status of candidate: Trang thai ung vien
    private Integer approvalStatus; // trang thai ho so ung vien da duoc duyet hay chua
    // Xem status: HrConstants.CandidateApprovalStatus
    private Integer examStatus; // trang thai ung vien co PASS bai test cua dot phong van hay khong
    // Xem status: HrConstants.CandidateExamStatus
    private Integer receptionStatus; // trạng thái của ứng viên sau khi đã PASS bài phỏng vấn/thi tuyển, trạng thái
    // này chỉ ứng viên có được nhận việc hay không


    private String refusalReason; // lý do từ chối
    @Column(name = "is_enterd_candidate_profile")
    private Boolean isEnterdCandidateProfile;//la ho so ung vien tu nhap link public ngoai/ ma nhan vien se tu sinh cho TH nay

    @Column(name = "probation_income")
    private Double probationIncome; // lương thử việc

    @Column(name = "basic_Income")
    private Double basicIncome; // lương chính thức

    @Column(name = "position_bonus")
    private Double positionBonus; // thưởng

    @Column(name = "allowance")
    private Double allowance; //trợ cấp

    @Column(name = "other_benefit")
    private Double otherBenefit; // khoản khác

    @Column(name = "personal_identification_number")
    protected String personalIdentificationNumber;
    @Column(name = "personal_identification_issueDate")
    protected Date personalIdentificationIssueDate;
    @Column(name = "personal_identification_issuePlace")
    protected String personalIdentificationIssuePlace;
    @Column(name = "is_send_mail_offer")
    private Boolean isSendMailOffer; // kết hợp với status = ACCEPT_OFFER thì mới ra được những ca gửi offer
    public Integer getPreScreenStatus() {
        return preScreenStatus;
    }

    public void setPreScreenStatus(Integer preScreenStatus) {
        this.preScreenStatus = preScreenStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public Recruitment getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public PositionTitle getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitle positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    public Double getDesiredPay() {
        return desiredPay;
    }

    public void setDesiredPay(Double desiredPay) {
        this.desiredPay = desiredPay;
    }

    public Date getPossibleWorkingDate() {
        return possibleWorkingDate;
    }

    public void setPossibleWorkingDate(Date possibleWorkingDate) {
        this.possibleWorkingDate = possibleWorkingDate;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getPermanentResidence() {
        return permanentResidence;
    }

    public void setPermanentResidence(String permanentResidence) {
        this.permanentResidence = permanentResidence;
    }

    public String getCurrentResidence() {
        return currentResidence;
    }

    public void setCurrentResidence(String currentResidence) {
        this.currentResidence = currentResidence;
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

    public AdministrativeUnit getAdministrativeUnit() {
        return administrativeUnit;
    }

    public void setAdministrativeUnit(AdministrativeUnit administrativeUnit) {
        this.administrativeUnit = administrativeUnit;
    }

    public Set<PersonCertificate> getCandidateCertificates() {
        return candidateCertificates;
    }

    public void setCandidateCertificates(Set<PersonCertificate> candidateCertificates) {
        this.candidateCertificates = candidateCertificates;
    }

    public String getRefusalReason() {
        return refusalReason;
    }

    public void setRefusalReason(String refusalReason) {
        this.refusalReason = refusalReason;
    }

    public Date getOnboardDate() {
        return onboardDate;
    }

    public void setOnboardDate(Date onboardDate) {
        this.onboardDate = onboardDate;
    }

    public Integer getOnboardStatus() {
        return onboardStatus;
    }

    public void setOnboardStatus(Integer onboardStatus) {
        this.onboardStatus = onboardStatus;
    }

    public Set<CandidateEducationHistory> getCandidateEducationHistory() {
        return candidateEducationHistory;
    }

    public void setCandidateEducationHistory(Set<CandidateEducationHistory> candidateEducationHistory) {
        this.candidateEducationHistory = candidateEducationHistory;
    }

    public Set<CandidateWorkingExperience> getCandidateWorkingExperience() {
        return candidateWorkingExperience;
    }

    public void setCandidateWorkingExperience(Set<CandidateWorkingExperience> candidateWorkingExperience) {
        this.candidateWorkingExperience = candidateWorkingExperience;
    }

    public Set<CandidateAttachment> getCandidateAttachments() {
        return candidateAttachments;
    }

    public void setCandidateAttachments(Set<CandidateAttachment> candidateAttachments) {
        this.candidateAttachments = candidateAttachments;
    }

    public Set<CandidateRecruitmentRound> getCandidateRecruitmentRounds() {
        return candidateRecruitmentRounds;
    }

    public void setCandidateRecruitmentRounds(Set<CandidateRecruitmentRound> candidateRecruitmentRounds) {
        this.candidateRecruitmentRounds = candidateRecruitmentRounds;
    }

    public HrOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganization organization) {
        this.organization = organization;
    }

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public Staff getIntroducer() {
        return introducer;
    }

    public void setIntroducer(Staff introducer) {
        this.introducer = introducer;
    }

    public RecruitmentPlan getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlan recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
    }

	public Boolean getIsEnterdCandidateProfile() {
		return isEnterdCandidateProfile;
	}

	public void setIsEnterdCandidateProfile(Boolean isEnterdCandidateProfile) {
		this.isEnterdCandidateProfile = isEnterdCandidateProfile;
	}

    public Double getOtherBenefit() {
        return otherBenefit;
    }

    public void setOtherBenefit(Double otherBenefit) {
        this.otherBenefit = otherBenefit;
    }

    public Double getAllowance() {
        return allowance;
    }

    public void setAllowance(Double allowance) {
        this.allowance = allowance;
    }

    public Double getPositionBonus() {
        return positionBonus;
    }

    public void setPositionBonus(Double positionBonus) {
        this.positionBonus = positionBonus;
    }

    public Double getBasicIncome() {
        return basicIncome;
    }

    public void setBasicIncome(Double basicIncome) {
        this.basicIncome = basicIncome;
    }

    public Double getProbationIncome() {
        return probationIncome;
    }

    public void setProbationIncome(Double probationIncome) {
        this.probationIncome = probationIncome;
    }

    public Boolean getEnterdCandidateProfile() {
        return isEnterdCandidateProfile;
    }

    public void setEnterdCandidateProfile(Boolean enterdCandidateProfile) {
        isEnterdCandidateProfile = enterdCandidateProfile;
    }

    public String getPersonalIdentificationNumber() {
        return personalIdentificationNumber;
    }

    public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
        this.personalIdentificationNumber = personalIdentificationNumber;
    }

    public Date getPersonalIdentificationIssueDate() {
        return personalIdentificationIssueDate;
    }

    public void setPersonalIdentificationIssueDate(Date personalIdentificationIssueDate) {
        this.personalIdentificationIssueDate = personalIdentificationIssueDate;
    }

    public String getPersonalIdentificationIssuePlace() {
        return personalIdentificationIssuePlace;
    }

    public void setPersonalIdentificationIssuePlace(String personalIdentificationIssuePlace) {
        this.personalIdentificationIssuePlace = personalIdentificationIssuePlace;
    }

    public Boolean getSendMailOffer() {
        return isSendMailOffer;
    }

    public void setSendMailOffer(Boolean sendMailOffer) {
        isSendMailOffer = sendMailOffer;
    }
}
