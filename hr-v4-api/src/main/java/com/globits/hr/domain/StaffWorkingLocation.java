package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.FileDescription;
import jakarta.persistence.*;

import java.util.Date;

// Địa điểm làm việc
@Entity
@Table(name = "tbl_staff_working_location")
public class StaffWorkingLocation extends BaseObject {
    private static final long serialVersionUID = -2208752009903206352L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff; // Nhân viên có địa điêm làm việc

    @Column(name="is_main_location")
    private Boolean isMainLocation; // Là địa điểm làm việc chính
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

	@Column(name = "working_location", length = 1000)
	private String workingLocation; // địa điêm làm việc

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public String getWorkingLocation() {
		return workingLocation;
	}

	public void setWorkingLocation(String workingLocation) {
		this.workingLocation = workingLocation;
	}

	public Boolean getIsMainLocation() {
		return isMainLocation;
	}

	public void setIsMainLocation(Boolean isMainLocation) {
		this.isMainLocation = isMainLocation;
	}

	public Workplace getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Workplace workplace) {
		this.workplace = workplace;
	}

}
