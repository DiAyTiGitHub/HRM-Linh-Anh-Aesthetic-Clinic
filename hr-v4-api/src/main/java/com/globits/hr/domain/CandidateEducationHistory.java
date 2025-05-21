package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Country;
import jakarta.persistence.*;

import java.util.Date;

/*
 * Quá trình đào tạo của ứng viên
 */

@Table(name = "tbl_candidate_education_history")
@Entity
public class CandidateEducationHistory extends BaseObject {
	private static final long serialVersionUID = 1L;
	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;
	@ManyToOne
	@JoinColumn(name = "hr_speciality_id")
	private HrSpeciality speciality;// Ngành đào tạo
	@ManyToOne
	@JoinColumn(name = "hr_major_id")
	private HrSpeciality major;// Chuyên ngành đào tạo
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
	private Integer status;// Trạng thái hiện thời
	@Column(name = "place")
	private String place;// Địa điểm đào tạo
	@Column(name = "note")
	private String note;// Ghi chú

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
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
