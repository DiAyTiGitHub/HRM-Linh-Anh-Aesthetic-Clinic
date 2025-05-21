package com.globits.timesheet.dto;

import com.globits.core.domain.BaseObject;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.ShiftRegistration;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

// Yêu cầu đăng ký ca làm việc
public class ShiftRegistrationDto extends BaseObjectDto {
    private StaffDto registerStaff; // nhân viên đăng ký
    private ShiftWorkDto shiftWork; // ca làm việc đăng ký
    private Date workingDate; // ngày làm việc
    private StaffDto approvalStaff; // nhân viên phê duyệt
    private Integer approvalStatus; // Trạng thái phê duyệt yêu cầu đăng ký làm việc. Chi tiết: HrConstants.ShiftRegistrationApprovalStatus
    private Integer workingType; // Loại làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingType
    private Double overtimeHours; // Thời gian đăng ký làm thêm. Có giá trị khi: workingType = EXTENDED_OVERTIME (Tăng ca kéo dài)

    public ShiftRegistrationDto() {
    }

    public ShiftRegistrationDto(ShiftRegistration entity) {
        super(entity);

        this.workingDate = entity.getWorkingDate();
        this.approvalStatus = entity.getApprovalStatus();
        this.workingType = entity.getWorkingType();
        this.overtimeHours = entity.getOvertimeHours();

        if (entity.getRegisterStaff() != null) {
            this.registerStaff = new StaffDto(entity.getRegisterStaff(), false);
        }

        if (entity.getShiftWork() != null) {
            this.shiftWork = new ShiftWorkDto(entity.getShiftWork());
        }
        if (entity.getApprovalStaff() != null) {
            this.approvalStaff = new StaffDto(entity.getApprovalStaff(), false);
        }
    }

    public StaffDto getRegisterStaff() {
        return registerStaff;
    }

    public void setRegisterStaff(StaffDto registerStaff) {
        this.registerStaff = registerStaff;
    }

    public ShiftWorkDto getShiftWork() {
        return shiftWork;
    }

    public void setShiftWork(ShiftWorkDto shiftWork) {
        this.shiftWork = shiftWork;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public StaffDto getApprovalStaff() {
        return approvalStaff;
    }

    public void setApprovalStaff(StaffDto approvalStaff) {
        this.approvalStaff = approvalStaff;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getWorkingType() {
        return workingType;
    }

    public void setWorkingType(Integer workingType) {
        this.workingType = workingType;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
}
