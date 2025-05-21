package com.globits.timesheet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.StaffWorkScheduleDto;

import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class TimeSheetStaffDto {
    private UUID staffId;
	private StaffDto staff; // Dùng cho mục đích hiển thị
    private Date workingDate;
    private StaffWorkScheduleDto staffWorkSchedule;
    private ShiftWorkTimePeriodDto shiftWorkTimePeriod;
    private Integer typeTimeSheetDetail; // 1 - Bắt đầu (Checkin); 2 - Kết thúc (Checkout)
    private Date currentTime; // thời điểm chấm

	//importExcel
	private String ipCheckOut;
	private String ipCheckIn;
	private String indexRowExcel;
	private Boolean isImportExcel;

	private String errorMessage; // Ghi chú chi tiết lỗi

    public TimeSheetStaffDto(){

    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

	public Date getWorkingDate() {
		return workingDate;
	}

	public void setWorkingDate(Date workingDate) {
		this.workingDate = workingDate;
	}

	public StaffWorkScheduleDto getStaffWorkSchedule() {
		return staffWorkSchedule;
	}

	public void setStaffWorkSchedule(StaffWorkScheduleDto staffWorkSchedule) {
		this.staffWorkSchedule = staffWorkSchedule;
	}

	public ShiftWorkTimePeriodDto getShiftWorkTimePeriod() {
		return shiftWorkTimePeriod;
	}

	public void setShiftWorkTimePeriod(ShiftWorkTimePeriodDto shiftWorkTimePeriod) {
		this.shiftWorkTimePeriod = shiftWorkTimePeriod;
	}

	public Integer getTypeTimeSheetDetail() {
		return typeTimeSheetDetail;
	}

	public void setTypeTimeSheetDetail(Integer typeTimeSheetDetail) {
		this.typeTimeSheetDetail = typeTimeSheetDetail;
	}

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}

	public String getIpCheckOut() {
		return ipCheckOut;
	}

	public void setIpCheckOut(String ipCheckOut) {
		this.ipCheckOut = ipCheckOut;
	}

	public String getIpCheckIn() {
		return ipCheckIn;
	}

	public void setIpCheckIn(String ipCheckIn) {
		this.ipCheckIn = ipCheckIn;
	}

	public StaffDto getStaff() {
		return staff;
	}

	public void setStaff(StaffDto staff) {
		this.staff = staff;
	}

	public Boolean getImportExcel() {
		return isImportExcel;
	}

	public void setImportExcel(Boolean importExcel) {
		isImportExcel = importExcel;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getIndexRowExcel() {
		return indexRowExcel;
	}

	public void setIndexRowExcel(String indexRowExcel) {
		this.indexRowExcel = indexRowExcel;
	}
	
}
