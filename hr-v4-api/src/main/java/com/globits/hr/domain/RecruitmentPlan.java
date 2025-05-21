package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.template.domain.ContentTemplate;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

// ke hoach tuyen dung
@Table(name = "tbl_recruitment_plan")
@Entity
public class RecruitmentPlan extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // ma ke hoach
    private String name; // ten ke hoach

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruitment_request_id")
    private RecruitmentRequest recruitmentRequest; // yeu cau

    //    private Integer quantity; // so luong thuc te
    @Column(name = "estimated_time_from")
    private Date estimatedTimeFrom; // thoi gian du kien tu

    @Column(name = "estimated_time_to")
    private Date estimatedTimeTo; // thoi gian du kien den

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description; // mo ta ke hoach

    @Column(name = "status")
    private Integer status; // trang thai: HrConstants.RecruitmentPlanStatus

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_approve_cv")
    private Staff personApproveCV;

    @Column(name = "posting_source", length = 4000)
    private String postingSource; // Nguồn đăng tuyển

    @OneToMany(mappedBy = "recruitmentPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Candidate> candidates;


    public String getPostingSource() {
        return postingSource;
    }

    public void setPostingSource(String postingSource) {
        this.postingSource = postingSource;
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

    public RecruitmentRequest getRecruitmentRequest() {
        return recruitmentRequest;
    }

    public void setRecruitmentRequest(RecruitmentRequest recruitmentRequest) {
        this.recruitmentRequest = recruitmentRequest;
    }

    public Date getEstimatedTimeFrom() {
        return estimatedTimeFrom;
    }

    public void setEstimatedTimeFrom(Date estimatedTimeFrom) {
        this.estimatedTimeFrom = estimatedTimeFrom;
    }

    public Date getEstimatedTimeTo() {
        return estimatedTimeTo;
    }

    public void setEstimatedTimeTo(Date estimatedTimeTo) {
        this.estimatedTimeTo = estimatedTimeTo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Staff getPersonApproveCV() {
        return personApproveCV;
    }

    public void setPersonApproveCV(Staff personApproveCV) {
        this.personApproveCV = personApproveCV;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(Set<Candidate> candidates) {
        this.candidates = candidates;
    }
}

