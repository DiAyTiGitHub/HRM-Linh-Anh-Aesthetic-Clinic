package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

// yeu cau tuyen dung
@Table(name = "tbl_recruitment_request")
@Entity
public class RecruitmentRequest extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // ma yeu cau tuyen dung
    private String name; // ten yeu cau

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private HrOrganization hrOrganization; // Đơn vị yêu cầu

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private HRDepartment hrDepartment; // phong ban

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private HRDepartment team; //Bộ phận/Nhóm:

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description; // mo ta cong viec

    @Column(name = "request", columnDefinition = "MEDIUMTEXT")
    private String request; // yeu cau

    private Integer status; // trang thái: HrConstants.RecruitmentRequestStatus

    @OneToMany(mappedBy = "recruitmentRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecruitmentRequestItem> recruitmentRequestItems; // Các vị trí cần tuyển trong yêu cầu

    @OneToMany(mappedBy = "recruitmentRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecruitmentPlan> recruitmentPlans; // Các kế hoạch tuyển dụng theo yêu cầu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_in_charge_id")
    private Staff personInCharge; // Hr Phụ trách yêu cầu tuyển dụng

    @ManyToOne
    @JoinColumn(name ="work_place_id")
    private Workplace workPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_position")
    private Position approvedPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_approve_position")
    private Position nextApprovePosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposer_id")
    private Staff proposer; // Người đề xuất

    @Column(name = "proposal_date")
    private Date proposalDate; // Ngày đề xuất

    @Column(name = "proposal_receipt_date")
    private Date proposalReceiptDate; // Ngày nhận đề xuất (ngày người phụ trách nhận đề xuất để thực hiện yêu cầu tuyển dụng)

    @Column(name = "recruiting_start_date")
    private Date recruitingStartDate;
    @Column(name = "recruiting_end_date")
    private Date recruitingEndDate;

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PositionRecruitmentRequest> positionRequests; // Các vị trí bị thay thế trong yêu cầu

    public Set<PositionRecruitmentRequest> getPositionRequests() {
        return positionRequests;
    }

    public void setPositionRequests(Set<PositionRecruitmentRequest> positionRequests) {
        this.positionRequests = positionRequests;
    }

    public Staff getProposer() {
        return proposer;
    }

    public void setProposer(Staff proposer) {
        this.proposer = proposer;
    }

    public Date getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(Date proposalDate) {
        this.proposalDate = proposalDate;
    }

    public Date getProposalReceiptDate() {
        return proposalReceiptDate;
    }

    public void setProposalReceiptDate(Date proposalReceiptDate) {
        this.proposalReceiptDate = proposalReceiptDate;
    }

    public Set<RecruitmentPlan> getRecruitmentPlans() {
        return recruitmentPlans;
    }

    public void setRecruitmentPlans(Set<RecruitmentPlan> recruitmentPlans) {
        this.recruitmentPlans = recruitmentPlans;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HRDepartment getHrDepartment() {
        return hrDepartment;
    }

    public void setHrDepartment(HRDepartment hrDepartment) {
        this.hrDepartment = hrDepartment;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public HrOrganization getHrOrganization() {
        return hrOrganization;
    }

    public void setHrOrganization(HrOrganization hrOrganization) {
        this.hrOrganization = hrOrganization;
    }

    public Set<RecruitmentRequestItem> getRecruitmentRequestItems() {
        return recruitmentRequestItems;
    }

    public void setRecruitmentRequestItems(Set<RecruitmentRequestItem> recruitmentRequestItems) {
        this.recruitmentRequestItems = recruitmentRequestItems;
    }

    public Staff getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(Staff personInCharge) {
        this.personInCharge = personInCharge;
    }

    public HRDepartment getTeam() {
        return team;
    }

    public void setTeam(HRDepartment team) {
        this.team = team;
    }

    public Workplace getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(Workplace workPlace) {
        this.workPlace = workPlace;
    }

    public Position getApprovedPosition() {
        return approvedPosition;
    }

    public void setApprovedPosition(Position approvedPosition) {
        this.approvedPosition = approvedPosition;
    }

    public Position getNextApprovePosition() {
        return nextApprovePosition;
    }

    public void setNextApprovePosition(Position nextApprovePosition) {
        this.nextApprovePosition = nextApprovePosition;
    }

    public Date getRecruitingStartDate() {
        return recruitingStartDate;
    }

    public void setRecruitingStartDate(Date recruitingStartDate) {
        this.recruitingStartDate = recruitingStartDate;
    }

    public Date getRecruitingEndDate() {
        return recruitingEndDate;
    }

    public void setRecruitingEndDate(Date recruitingEndDate) {
        this.recruitingEndDate = recruitingEndDate;
    }
}

