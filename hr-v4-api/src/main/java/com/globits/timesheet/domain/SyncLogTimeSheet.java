package com.globits.timesheet.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.globits.hr.domain.TimeSheetStaff;
import com.globits.core.domain.BaseObject;
import com.globits.hr.data.types.TimeSheetRegStatus;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.domain.WorkingStatus;


@Table(name = "tbl_sync_log_time_sheet")
@Entity
public class SyncLogTimeSheet extends BaseObject {
	private static final long serialVersionUID = 1L;
	
	
	@Column(name="date_sync")
	private Date dateSync;//Thời điểm bắt đầu làm việc
	@Column(columnDefinition = "JSON")
	private String staffResponse;
	@Column(name="number_api")
	private Integer numberApi;// so ban ghi cham cong lay tu api 
	@Column(name="number_record")
	private Integer numberRecord;// so ban ghi cham cong duoc tao 
	@Column(name="status")
	private String status;// ERROR ,SUCCESS
	public Date getDateSync() {
		return dateSync;
	}
	public void setDateSync(Date dateSync) {
		this.dateSync = dateSync;
	}
	public String getStaffResponse() {
		return staffResponse;
	}
	public void setStaffResponse(String staffResponse) {
		this.staffResponse = staffResponse;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getNumberApi() {
		return numberApi;
	}
	public void setNumberApi(Integer numberApi) {
		this.numberApi = numberApi;
	}
	public Integer getNumberRecord() {
		return numberRecord;
	}
	public void setNumberRecord(Integer numberRecord) {
		this.numberRecord = numberRecord;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
		
}
