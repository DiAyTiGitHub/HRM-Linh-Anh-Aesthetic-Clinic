package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Country;

import jakarta.persistence.*;

import java.util.Date;

// quá trình bồi dưỡng nhân viên
@Entity
@Table(name = "tbl_staff_training_history")
public class StaffTrainingHistory extends BaseObject {
	@Column(name = "start_date")
	private Date startDate;
	@Column(name = "end_date")
	private Date endDate;
	@Column(name = "training_place")
	private String trainingPlace;
	@Column(name = "training_content")
	private String trainingContent;
	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country trainingCountry;
	@ManyToOne
	@JoinColumn(name = "certificate")
	private Certificate certificate;
	@ManyToOne
	@JoinColumn(name = "staff_id")
	private Staff staff;

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

	public String getTrainingPlace() {
		return trainingPlace;
	}

	public void setTrainingPlace(String trainingPlace) {
		this.trainingPlace = trainingPlace;
	}

	public String getTrainingContent() {
		return trainingContent;
	}

	public void setTrainingContent(String trainingContent) {
		this.trainingContent = trainingContent;
	}

	public Country getTrainingCountry() {
		return trainingCountry;
	}

	public void setTrainingCountry(Country trainingCountry) {
		this.trainingCountry = trainingCountry;
	}

	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}
}
