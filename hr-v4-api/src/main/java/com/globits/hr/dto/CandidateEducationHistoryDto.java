package com.globits.hr.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.globits.core.domain.Country;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.CountryDto;
import com.globits.hr.domain.*;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.checkerframework.checker.units.qual.C;

public class CandidateEducationHistoryDto extends BaseObjectDto {
    private UUID candidateId;
    private HrSpecialityDto speciality;// Ngành đào tạo
    private HrSpecialityDto major;// Chuyên ngành đào tạo
    private HrEducationTypeDto educationType; // loại hình đào tạo
    private EducationalInstitutionDto educationalInstitution;
    private EducationDegreeDto educationDegree;// bằng cấp
    private CountryDto country; // quốc gia đào tạo
    private Date startDate;// năm bắt đầu
    private Date endDate;// năm kết thúc
    private String schoolName; // cớ sở đào tạo
    private String description;// mô tả
    private Integer status;// Trạng thái hiện thời
    private String place;// Địa điểm đào tạo
    private String note;// Ghi chú

    public CandidateEducationHistoryDto() {
        super();
    }

    public CandidateEducationHistoryDto(CandidateEducationHistory entity) {
        if (entity == null) return;

        this.id = entity.getId();
        if (entity.getCandidate() != null) {
            this.candidateId = entity.getCandidate().getId();
        }
        this.schoolName = entity.getSchoolName();
        this.description = entity.getDescription();
        this.status = entity.getStatus();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (entity.getStartDate() != null) {
                if (entity.getStartDate().before(sdf.parse("01-01-1900"))
                        || entity.getStartDate().after(sdf.parse("01-01-2100"))) {
                    this.startDate = null;
                } else {
                    this.startDate = entity.getStartDate();
                }
            }
            if (entity.getEndDate() != null) {
                if (entity.getEndDate().before(sdf.parse("01-01-1900"))
                        || entity.getEndDate().after(sdf.parse("01-01-2100"))) {
                    this.endDate = null;
                } else {
                    this.endDate = entity.getEndDate();
                }
            }
        } catch (Exception e) {
        }
        if (entity.getCountry() != null) {
            this.country = new CountryDto(entity.getCountry());
        }
        if (entity.getSpeciality() != null) {
            this.speciality = new HrSpecialityDto(entity.getSpeciality());
        }
        if (entity.getEducationType() != null) {
            this.educationType = new HrEducationTypeDto(entity.getEducationType());
        }
        if (entity.getEducationDegree() != null) {
            this.educationDegree = new EducationDegreeDto(entity.getEducationDegree());
        }
        if (entity.getEducationalInstitution() != null) {
            this.educationalInstitution = new EducationalInstitutionDto(
                    entity.getEducationalInstitution());
        }
        if (entity.getMajor() != null) {
            this.major = new HrSpecialityDto(entity.getMajor());
        }
        this.place = entity.getPlace();// Địa điểm đào tạo
        this.note = entity.getNote();// Ghi chú
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
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

    public HrSpecialityDto getSpeciality() {
        return speciality;
    }

    public void setSpeciality(HrSpecialityDto speciality) {
        this.speciality = speciality;
    }

    public HrSpecialityDto getMajor() {
        return major;
    }

    public void setMajor(HrSpecialityDto major) {
        this.major = major;
    }

    public HrEducationTypeDto getEducationType() {
        return educationType;
    }

    public void setEducationType(HrEducationTypeDto educationType) {
        this.educationType = educationType;
    }

    public EducationalInstitutionDto getEducationalInstitution() {
        return educationalInstitution;
    }

    public void setEducationalInstitution(EducationalInstitutionDto educationalInstitution) {
        this.educationalInstitution = educationalInstitution;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
