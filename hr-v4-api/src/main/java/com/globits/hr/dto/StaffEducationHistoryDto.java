package com.globits.hr.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.CountryDto;
import com.globits.hr.domain.StaffEducationHistory;

public class StaffEducationHistoryDto extends BaseObjectDto {
    private StaffDto staff;
    private Date startDate;
    private Date actualGraduationYear;
    private Date endDate;
    private String schoolName;
    private String description;
    private Integer status;// Trạng thái hiện thời
    private CountryDto country; // quốc gia đào tạo
    private EducationalInstitutionDto educationalInstitution; // cơ sở đào tạo
    private HrSpecialityDto speciality;// chuyên ngày đào tạo
    private HrEducationTypeDto educationType;// hình thức đào tạo
    private EducationDegreeDto educationDegree;// bằng cấp
    private Boolean isCurrent;// Là quá trình hiện thời
    private String place;// Địa điểm đào tạo
    private String decisionCode;// Số quyết định cho đi học
    private String fundingSource;// Nguồn kinh phí
    private String note;// Ghi chú
    private Boolean isConfirmation;// xác nhận
    private Boolean isCountedForSeniority;// được tính thâm niên
    private String basis;// căn cứ
    private Date decisionDate;// Ngày quyết định
    private Date returnDate;// Năm tiếp nhận về (kết thúc quá trình học, tiếp nhận trở lại trường)
    private Boolean notFinish;// Chưa kết thúc
    private Date finishDateByDecision;// Ngày kết thúc theo quyết định
    private Boolean isExtended;// ĐƯợc gia hạn
    private String extendDecisionCode;// Số quyết định gia hạn
    private Date extendDecisionDate;// Ngày ban hành quyết định gia hạn
    private Date extendDateByDecision;// Gia han đến ngày
    private HrSpecialityDto major;// Chuyên ngành đào tạo

    // import
    private String staffCode;
    private String countryCode;
    private String specialityCode;
    private String educationTypeName;
    private String educationTypeCode;
    private String educationDegreeCode;
    private String majorCode;

    public StaffEducationHistoryDto() {
        super();
    }

    public StaffEducationHistoryDto(StaffEducationHistory educationHistory) {
        if (educationHistory != null) {
            this.id = educationHistory.getId();
            this.staff = new StaffDto(educationHistory.getStaff(), false);
            this.schoolName = educationHistory.getSchoolName();
            this.description = educationHistory.getDescription();
            this.status = educationHistory.getStatus();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if (educationHistory.getStartDate() != null) {
                    if (educationHistory.getStartDate().before(sdf.parse("01-01-1900"))
                            || educationHistory.getStartDate().after(sdf.parse("01-01-2100"))) {
                        this.startDate = null;
                    } else {
                        this.startDate = educationHistory.getStartDate();
                    }
                }
                if (educationHistory.getEndDate() != null) {
                    if (educationHistory.getEndDate().before(sdf.parse("01-01-1900"))
                            || educationHistory.getEndDate().after(sdf.parse("01-01-2100"))) {
                        this.endDate = null;
                    } else {
                        this.endDate = educationHistory.getEndDate();
                    }
                }
            } catch (Exception e) {
            }
            if (educationHistory.getCountry() != null) {
                this.country = new CountryDto(educationHistory.getCountry());
            }
            if (educationHistory.getSpeciality() != null) {
                this.speciality = new HrSpecialityDto(educationHistory.getSpeciality());
            }
            if (educationHistory.getEducationType() != null) {
                this.educationType = new HrEducationTypeDto(educationHistory.getEducationType());
            }
            if (educationHistory.getEducationDegree() != null) {
                this.educationDegree = new EducationDegreeDto(educationHistory.getEducationDegree());
            }
            if (educationHistory.getEducationalInstitution() != null) {
                this.educationalInstitution = new EducationalInstitutionDto(
                        educationHistory.getEducationalInstitution());
            }
            if (educationHistory.getMajor() != null) {
                this.major = new HrSpecialityDto(educationHistory.getMajor());
            }
            this.isCurrent = educationHistory.getIsCurrent();// Là quá trình hiện thời
            this.place = educationHistory.getPlace();// Địa điểm đào tạo
            this.decisionCode = educationHistory.getDecisionCode();// Số quyết định cho đi học
            this.fundingSource = educationHistory.getFundingSource();// Nguồn kinh phí
            this.note = educationHistory.getNote();// Ghi chú
            this.isConfirmation = educationHistory.getIsConfirmation();// xác nhận
            this.isCountedForSeniority = educationHistory.getIsCountedForSeniority();// được tính thâm niên
            this.basis = educationHistory.getBasis();// căn cứ
            this.decisionDate = educationHistory.getDecisionDate();// Ngày quyết định
            this.returnDate = educationHistory.getReturnDate();// Năm tiếp nhận về (kết thúc quá trình học, tiếp nhận
                                                               // trở
                                                               // lại trường)
            this.notFinish = educationHistory.getNotFinish();// Chưa kết thúc
            this.finishDateByDecision = educationHistory.getFinishDateByDecision();// Ngày kết thúc theo quyết định
            this.isExtended = educationHistory.getIsExtended();// ĐƯợc gia hạn
            this.extendDecisionCode = educationHistory.getExtendDecisionCode();// Số quyết định gia hạn
            this.extendDecisionDate = educationHistory.getExtendDecisionDate();// Ngày ban hành quyết định gia hạn
            this.extendDateByDecision = educationHistory.getExtendDateByDecision();
            this.actualGraduationYear = educationHistory.getActualGraduationYear();
        }

    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
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

    public CountryDto getCountry() {
        return country;
    }

    public void setCountry(CountryDto country) {
        this.country = country;
    }

    public Date getActualGraduationYear() {
        return actualGraduationYear;
    }

    public void setActualGraduationYear(Date actualGraduationYear) {
        this.actualGraduationYear = actualGraduationYear;
    }

    public EducationalInstitutionDto getEducationalInstitution() {
        return educationalInstitution;
    }

    public void setEducationalInstitution(EducationalInstitutionDto educationalInstitution) {
        this.educationalInstitution = educationalInstitution;
    }

    public HrSpecialityDto getSpeciality() {
        return speciality;
    }

    public void setSpeciality(HrSpecialityDto speciality) {
        this.speciality = speciality;
    }

    public HrEducationTypeDto getEducationType() {
        return educationType;
    }

    public void setEducationType(HrEducationTypeDto educationType) {
        this.educationType = educationType;
    }

    public EducationDegreeDto getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(EducationDegreeDto educationDegree) {
        this.educationDegree = educationDegree;
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

    public void setExtendDateByDecision(Date extendDateByDecision) {
        this.extendDateByDecision = extendDateByDecision;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEducationTypeName() {
        return educationTypeName;
    }

    public void setEducationTypeName(String educationTypeName) {
        this.educationTypeName = educationTypeName;
    }

    public String getEducationTypeCode() {
        return educationTypeCode;
    }

    public void setEducationTypeCode(String educationTypeCode) {
        this.educationTypeCode = educationTypeCode;
    }

    public String getEducationDegreeCode() {
        return educationDegreeCode;
    }

    public void setEducationDegreeCode(String educationDegreeCode) {
        this.educationDegreeCode = educationDegreeCode;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
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

    public Boolean getIsExtended() {
        return isExtended;
    }

    public void setIsExtended(Boolean isExtended) {
        this.isExtended = isExtended;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }

    public HrSpecialityDto getMajor() {
        return major;
    }

    public void setMajor(HrSpecialityDto major) {
        this.major = major;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

}
