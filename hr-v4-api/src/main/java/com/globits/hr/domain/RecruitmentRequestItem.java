package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.utils.Const;
import jakarta.persistence.*;

// Các vị trí cần tuyển trong yêu cầu tuyển dụng
@Table(name = "tbl_recruitment_request_item")
@Entity
public class RecruitmentRequestItem extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruitment_request_id")
    private RecruitmentRequest recruitmentRequest; // Thuộc yêu cầu tuyển dụng nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_title_id")
    private PositionTitle positionTitle; // Vị trí cần tuyển

    @Column(name = "in_plan_quantity")
    private Integer inPlanQuantity; // Số lượng trong định biên

    @Column(name = "extra_quantity")
    private Integer extraQuantity = 0; // Số lượng tuyển lọc = Số lượng tuyển ngoài định biên

    @Column(name = "total_quantity")
    private Integer totalQuantity; // Tổng số lượng đề nghị tuyển = inPlanQuantity + extraQuantity

    @Column(name = "announcement_quantity")
    private Integer announcementQuantity; // Số lượng đăng tuyển/Thông báo tuyển
    @Column(name = "professional_level")
    private String professionalLevel; // Trình độ chuyên môn:
    @Column(name = "professional_skills")
    private String professionalSkills; //Kỹ năng chuyên môn:
    @Column(name = "gender")
    private String gender;// Giới tính
    @Column(name = "weight")
    private Double weight;// Cân nặng
    @Column(name = "height")
    private Double height;//Chiều cao
    @Column(name = "year_of_experience")
    private Integer yearOfExperience;//Số năm kinh nghiệm liên quan đến vị trí tuyển:
    @Column(name = "other_requirements")
    private String otherRequirements;
    //Độ tuổi:
    @Column(name = "minimum_age")
    private Integer minimumAge;
    @Column(name = "maxium_age")
    private Integer maximumAge;
    //Thu nhập đề xuất
    @Column(name = "minium_income")
    private Double minimumIncome;
    @Column(name = "maximum_income")
    private Double maximumIncome;
    //true	Tuyển dụng trong định biên
    //false	Tuyển dụng mới ngoài định biên
    @Column(name = "is_within_headcount")
    private Boolean isWithinHeadcount;
    @Column(name = "is_replacement_recruitment")
    private Boolean isReplacementRecruitment;
    //false	Tuyển dụng mới ngoài định biên
    @Column(name = "reason")
    private String reason;



    @ManyToOne
    @JoinColumn(name = "replaced_person_id")
    private Staff replacedPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type")
    private Const.WorkType workType;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description; // mo ta cong viec

    @Column(name = "request", columnDefinition = "MEDIUMTEXT")
    private String request; // yeu cau

    public RecruitmentRequest getRecruitmentRequest() {
        return recruitmentRequest;
    }

    public void setRecruitmentRequest(RecruitmentRequest recruitmentRequest) {
        this.recruitmentRequest = recruitmentRequest;
    }

    public PositionTitle getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitle positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Integer getInPlanQuantity() {
        return inPlanQuantity;
    }

    public void setInPlanQuantity(Integer inPlanQuantity) {
        this.inPlanQuantity = inPlanQuantity;
    }

    public Integer getExtraQuantity() {
        return extraQuantity;
    }

    public void setExtraQuantity(Integer extraQuantity) {
        this.extraQuantity = extraQuantity;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getAnnouncementQuantity() {
        return announcementQuantity;
    }

    public void setAnnouncementQuantity(Integer announcementQuantity) {
        this.announcementQuantity = announcementQuantity;
    }

    public String getProfessionalLevel() {
        return professionalLevel;
    }

    public void setProfessionalLevel(String professionalLevel) {
        this.professionalLevel = professionalLevel;
    }

    public String getProfessionalSkills() {
        return professionalSkills;
    }

    public void setProfessionalSkills(String professionalSkills) {
        this.professionalSkills = professionalSkills;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getYearOfExperience() {
        return yearOfExperience;
    }

    public void setYearOfExperience(Integer yearOfExperience) {
        this.yearOfExperience = yearOfExperience;
    }

    public String getOtherRequirements() {
        return otherRequirements;
    }

    public void setOtherRequirements(String otherRequirements) {
        this.otherRequirements = otherRequirements;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }

    public Double getMinimumIncome() {
        return minimumIncome;
    }

    public void setMinimumIncome(Double minimumIncome) {
        this.minimumIncome = minimumIncome;
    }

    public Double getMaximumIncome() {
        return maximumIncome;
    }

    public void setMaximumIncome(Double maximumIncome) {
        this.maximumIncome = maximumIncome;
    }

    public Boolean getWithinHeadcount() {
        return isWithinHeadcount;
    }

    public void setWithinHeadcount(Boolean withinHeadcount) {
        isWithinHeadcount = withinHeadcount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Const.WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(Const.WorkType workType) {
        this.workType = workType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Staff getReplacedPerson() {
        return replacedPerson;
    }

    public void setReplacedPerson(Staff replacedPerson) {
        this.replacedPerson = replacedPerson;
    }

    public Boolean getReplacementRecruitment() {
        return isReplacementRecruitment;
    }

    public void setReplacementRecruitment(Boolean replacementRecruitment) {
        isReplacementRecruitment = replacementRecruitment;
    }
}

