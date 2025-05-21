package com.globits.hr.dto;

import com.globits.core.domain.BaseObject;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.PositionTitle;
import com.globits.hr.domain.RecruitmentRequest;
import com.globits.hr.domain.RecruitmentRequestItem;
import com.globits.hr.utils.Const;
import jakarta.persistence.*;

public class RecruitmentRequestItemDto extends BaseObjectDto {
    private RecruitmentRequestDto recruitmentRequest; // Thuộc yêu cầu tuyển dụng nào

    private PositionTitleDto positionTitle; // Vị trí cần tuyển

    private Integer inPlanQuantity = 0; // Số lượng trong định biên

    private Integer extraQuantity = 0; // Số lượng tuyển lọc = Số lượng tuyển ngoài định biên

    private Integer announcementQuantity; // Số lượng đăng tuyển/Thông báo tuyển
    private String professionalLevel; // Trình độ chuyên môn:
    private String professionalSkills; //Kỹ năng chuyên môn:
    private String gender;// Giới tính
    private Double weight;// Cân nặng
    private Double height;//Chiều cao
    private Integer yearOfExperience;//Số năm kinh nghiệm liên quan đến vị trí tuyển:
    private String otherRequirements;
    //Độ tuổi:
    private Integer minimumAge;
    private Integer maximumAge;
    //Thu nhập đề xuất
    private Double minimumIncome;
    private Double maximumIncome;
    //true	Tuyển dụng trong định biên
    //false	Tuyển dụng mới ngoài định biên
    private Boolean isWithinHeadcount;
    // true = tuyển thay thế, false = tuyển mới
    private Boolean isReplacementRecruitment;
    private StaffDto replacedPerson;
    //false	Tuyển dụng mới ngoài định biên
    private String reason;
    private Const.WorkType workType;
    private String description; // mo ta cong viec
    private String request; // yeu cau
    public RecruitmentRequestItemDto(RecruitmentRequestItem entity) {
        super(entity);

        this.inPlanQuantity = entity.getInPlanQuantity();
        this.extraQuantity = entity.getExtraQuantity();
        this.announcementQuantity = entity.getAnnouncementQuantity();

        if(entity.getPositionTitle() != null) {
            this.positionTitle = new PositionTitleDto(entity.getPositionTitle());
        }
        this.professionalLevel = entity.getProfessionalLevel();
        this.professionalSkills = entity.getProfessionalSkills();
        this.gender = entity.getGender();
        this.weight = entity.getWeight();
        this.height = entity.getHeight();
        this.yearOfExperience = entity.getYearOfExperience();
        this.otherRequirements = entity.getOtherRequirements();
        this.minimumAge = entity.getMinimumAge();
        this.maximumAge = entity.getMaximumAge();
        this.minimumIncome = entity.getMinimumIncome();
        this.maximumIncome = entity.getMaximumIncome();
        this.isWithinHeadcount = entity.getWithinHeadcount();
        this.isReplacementRecruitment = entity.getReplacementRecruitment();
        this.replacedPerson = new StaffDto(entity.getReplacedPerson());
        this.reason = entity.getReason();
        this.workType = entity.getWorkType();
        this.description = entity.getDescription();
        this.request = entity.getRequest();
    }

    public RecruitmentRequestItemDto() {
    }

    public RecruitmentRequestItemDto(BaseObject entity) {
        super(entity);
    }

    public RecruitmentRequestDto getRecruitmentRequest() {
        return recruitmentRequest;
    }

    public void setRecruitmentRequest(RecruitmentRequestDto recruitmentRequest) {
        this.recruitmentRequest = recruitmentRequest;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
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

    public Boolean getIsWithinHeadcount() {
        return isWithinHeadcount;
    }

    public void setIsWithinHeadcount(Boolean withinHeadcount) {
        this.isWithinHeadcount = withinHeadcount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getIsReplacementRecruitment() {
        return isReplacementRecruitment;
    }

    public void setIsReplacementRecruitment(Boolean replacementRecruitment) {
        this.isReplacementRecruitment = replacementRecruitment;
    }

    public StaffDto getReplacedPerson() {
        return replacedPerson;
    }

    public void setReplacedPerson(StaffDto replacedPerson) {
        this.replacedPerson = replacedPerson;
    }

    public Const.WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(Const.WorkType workType) {
        this.workType = workType;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

