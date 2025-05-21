package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.timesheet.domain.AbsenceRequest;
import jakarta.persistence.Column;

import java.util.Date;

public class AbsenceRequestDto extends BaseObjectDto {
	private StaffDto staff;
    private StaffWorkScheduleDto workSchedule; // yêu cầu nghỉ vào lịch nào
    private Date requestDate; // ngày tạo yêu cầu nghỉ
    private String absenceReason; // Lý do yêu cầu
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.AbsenceRequestApprovalStatus
    private Integer absenceType; // Loại nghỉ phép. Chi tiết: HrConstants.AbsenceRequestType

    public AbsenceRequestDto(AbsenceRequest entity, Boolean isGetFull) {
        super(entity);
        if (entity == null) return;
        this.requestDate = entity.getRequestDate();
        this.absenceReason = entity.getAbsenceReason();
        this.approvalStatus = entity.getApprovalStatus();
        this.absenceType = entity.getAbsenceType();

        if (isGetFull) {
            if (entity.getWorkSchedule() != null) {
                this.workSchedule = new StaffWorkScheduleDto(entity.getWorkSchedule());
                if (entity.getWorkSchedule().getStaff() != null) {
                	this.staff = new StaffDto(entity.getWorkSchedule().getStaff(), false);
                }
            }
        }
    }

    public AbsenceRequestDto(AbsenceRequest entity) {
        this(entity, true);
    }

    public AbsenceRequestDto() {
    }

    public StaffWorkScheduleDto getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(StaffWorkScheduleDto workSchedule) {
        this.workSchedule = workSchedule;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getAbsenceReason() {
        return absenceReason;
    }

    public void setAbsenceReason(String absenceReason) {
        this.absenceReason = absenceReason;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

	public StaffDto getStaff() {
		return staff;
	}

	public void setStaff(StaffDto staff) {
		this.staff = staff;
	}

    public Integer getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(Integer absenceType) {
        this.absenceType = absenceType;
    }
}
