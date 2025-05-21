package com.globits.hr.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Country;

/*
 * Quá trình đào tạo của nhân sự
 */
@Table(name = "tbl_staff_education_history")
@Entity
public class StaffEducationHistory extends BaseObject {
    private static final long serialVersionUID = 1L;
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "hr_speciality_id")
    private HrSpeciality speciality;//Ngành đào tạo

    @ManyToOne
    @JoinColumn(name = "hr_major_id")
    private HrSpeciality major;//Chuyên ngành đào tạo

    @ManyToOne
    @JoinColumn(name = "hr_education_type_id")
    private HrEducationType educationType; // loại hình đào tạo
    @ManyToOne
    @JoinColumn(name = "educational_institution_id")
    private EducationalInstitution educationalInstitution;
    @ManyToOne
    @JoinColumn(name = "education_degree_id")
    private EducationDegree educationDegree;// bằng cấp

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country; // quốc gia đào tạo

    @Column(name = "start_date") // năm nhập học theo qđ
    private Date startDate;// năm bắt đầu

    @Column(name = "end_date") // năm tốt nghiệp theo qđ
    private Date endDate;// năm kết thúc

    @Column(name = "school_name")
    private String schoolName; // cớ sở đào tạo

    @Column(name = "description")
    private String description;// mô tả

    @Column(name = "status")
    private Integer status;//Trạng thái hiện thời

    @Column(name = "is_current")
    private Boolean isCurrent;//Là quá trình hiện thời

    @Column(name = "place")
    private String place;//Địa điểm đào tạo

    @Column(name = "decision_code")
    private String decisionCode;// Số quyết định cho đi học

    @Column(name = "funding_source")
    private String fundingSource;// Nguồn kinh phí

    @Column(name = "note")
    private String note;// Ghi chú

    @Column(name = "is_confirmation")
    private Boolean isConfirmation;// xác nhận

    @Column(name = "is_counted_for_seniority")
    private Boolean isCountedForSeniority;//được tính thâm niên

    @Column(name = "basis")
    private String basis;// căn cứ

    @Column(name = "decisionDate")
    private Date decisionDate;// Ngày quyết định

    @Column(name = "return_date")
    private Date returnDate;// Năm tiếp nhận về (kết thúc quá trình học, tiếp nhận trở lại trường)

    @Column(name = "not_finish")
    private Boolean notFinish;//Chưa kết thúc
    @Column(name = "actual_graduation_year")
    private Date actualGraduationYear;

    @Column(name = "finish_date_by_decision")
    private Date finishDateByDecision;//Ngày kết thúc theo quyết định

    @Column(name = "is_extended")
    private Boolean isExtended;//ĐƯợc gia hạn

    @Column(name = "extend_decision_code")
    private String extendDecisionCode;//Số quyết định gia hạn

    @Column(name = "extend_decision_date")
    private Date extendDecisionDate;//Ngày ban hành quyết định gia hạn

    @Column(name = "extend_date_by_decision")
    private Date extendDateByDecision;//Gia hạn tới ngày (theo quyết định gia hạn)

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public HrSpeciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(HrSpeciality speciality) {
        this.speciality = speciality;
    }

    public EducationalInstitution getEducationalInstitution() {
        return educationalInstitution;
    }

    public void setEducationalInstitution(EducationalInstitution educationalInstitution) {
        this.educationalInstitution = educationalInstitution;
    }

    public HrSpeciality getMajor() {
        return major;
    }

    public void setMajor(HrSpeciality major) {
        this.major = major;
    }

    public HrEducationType getEducationType() {
        return educationType;
    }

    public void setEducationType(HrEducationType educationType) {
        this.educationType = educationType;
    }

    public EducationDegree getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(EducationDegree educationDegree) {
        this.educationDegree = educationDegree;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDecisionCode() {
        return decisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        this.decisionCode = decisionCode;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getIsConfirmation() {
        return isConfirmation;
    }

    public void setIsConfirmation(Boolean isConfirmation) {
        this.isConfirmation = isConfirmation;
    }

    public Boolean getIsCountedForSeniority() {
        return isCountedForSeniority;
    }

    public void setIsCountedForSeniority(Boolean isCountedForSeniority) {
        this.isCountedForSeniority = isCountedForSeniority;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Boolean getNotFinish() {
        return notFinish;
    }

    public void setNotFinish(Boolean notFinish) {
        this.notFinish = notFinish;
    }

    public Date getFinishDateByDecision() {
        return finishDateByDecision;
    }

    public void setFinishDateByDecision(Date finishDateByDecision) {
        this.finishDateByDecision = finishDateByDecision;
    }

    public Boolean getIsExtended() {
        return isExtended;
    }

    public void setIsExtended(Boolean isExtended) {
        this.isExtended = isExtended;
    }

    public String getExtendDecisionCode() {
        return extendDecisionCode;
    }

    public void setExtendDecisionCode(String extendDecisionCode) {
        this.extendDecisionCode = extendDecisionCode;
    }

    public Date getExtendDecisionDate() {
        return extendDecisionDate;
    }

    public void setExtendDecisionDate(Date extendDecisionDate) {
        this.extendDecisionDate = extendDecisionDate;
    }

    public Date getExtendDateByDecision() {
        return extendDateByDecision;
    }

    public Date getActualGraduationYear() {
        return actualGraduationYear;
    }

    public void setActualGraduationYear(Date actualGraduationYear) {
        this.actualGraduationYear = actualGraduationYear;
    }

    public void setExtendDateByDecision(Date extendDateByDecision) {
        this.extendDateByDecision = extendDateByDecision;
    }
}
