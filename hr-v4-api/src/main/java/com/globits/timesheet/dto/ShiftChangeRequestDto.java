package com.globits.timesheet.dto;

import java.util.Date;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.ShiftChangeRequest;

// Yêu cầu đăng ký ca làm việc
public class ShiftChangeRequestDto extends BaseObjectDto {

    // CA LÀM VIỆC CẦN THAY ĐỔI
    private ShiftWorkDto fromShiftWork; // ca làm việc cần thay đổi
    private Date fromWorkingDate; // ngày làm việc cần thay đổi

    // CA LÀM VIỆC ĐƯỢC YÊU CẦU ĐỔI
    private ShiftWorkDto toShiftWork; // ca làm việc được yêu cầu đổi
    private Date toWorkingDate; // ngày làm việc được yêu cầu đổi

    private StaffDto registerStaff; // nhân viên yêu cầu đổi ca
    private Date requestDate; // ngày tạo yêu cầu đổi ca
    private String requestReason; // Lý do yêu cầu
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.ShiftChangeRequestApprovalStatus
    private StaffDto approvalStaff; // nhân viên phê duyệt

    public ShiftChangeRequestDto() {
    }

    public ShiftChangeRequestDto(ShiftChangeRequest entity) {
        super(entity);

        this.fromWorkingDate = entity.getFromWorkingDate();
        this.toWorkingDate = entity.getToWorkingDate();
        this.requestReason = entity.getRequestReason();
        this.requestDate = entity.getRequestDate();
        this.approvalStatus = entity.getApprovalStatus();

        if (entity.getFromShiftWork() != null) {
            this.fromShiftWork = new ShiftWorkDto();
            this.fromShiftWork.setId(entity.getFromShiftWork().getId());
            this.fromShiftWork.setCode(entity.getFromShiftWork().getCode());
            this.fromShiftWork.setName(entity.getFromShiftWork().getName());
            this.fromShiftWork.setTotalHours(entity.getFromShiftWork().getTotalHours());
            this.fromShiftWork.setConvertedWorkingHours(entity.getFromShiftWork().getConvertedWorkingHours());
        }

        if (entity.getToShiftWork() != null) {
            this.toShiftWork = new ShiftWorkDto();
            this.toShiftWork.setId(entity.getToShiftWork().getId());
            this.toShiftWork.setName(entity.getToShiftWork().getName());
            this.toShiftWork.setCode(entity.getToShiftWork().getCode());
            this.toShiftWork.setTotalHours(entity.getToShiftWork().getTotalHours());
            this.toShiftWork.setConvertedWorkingHours(entity.getToShiftWork().getConvertedWorkingHours());
        }

        if (entity.getRegisterStaff() != null) {
            this.registerStaff = new StaffDto();

            this.registerStaff.setId(entity.getRegisterStaff().getId());
            this.registerStaff.setStaffCode(entity.getRegisterStaff().getStaffCode());
            this.registerStaff.setDisplayName(entity.getRegisterStaff().getDisplayName());
        }

        if (entity.getApprovalStaff() != null) {
            this.approvalStaff = new StaffDto();

            this.approvalStaff.setId(entity.getApprovalStaff().getId());
            this.approvalStaff.setStaffCode(entity.getApprovalStaff().getStaffCode());
            this.approvalStaff.setDisplayName(entity.getApprovalStaff().getDisplayName());
        }
    }


    public ShiftWorkDto getFromShiftWork() {
        return fromShiftWork;
    }

    public void setFromShiftWork(ShiftWorkDto fromShiftWork) {
        this.fromShiftWork = fromShiftWork;
    }

    public Date getFromWorkingDate() {
        return fromWorkingDate;
    }

    public void setFromWorkingDate(Date fromWorkingDate) {
        this.fromWorkingDate = fromWorkingDate;
    }

    public ShiftWorkDto getToShiftWork() {
        return toShiftWork;
    }

    public void setToShiftWork(ShiftWorkDto toShiftWork) {
        this.toShiftWork = toShiftWork;
    }

    public Date getToWorkingDate() {
        return toWorkingDate;
    }

    public void setToWorkingDate(Date toWorkingDate) {
        this.toWorkingDate = toWorkingDate;
    }

    public StaffDto getRegisterStaff() {
        return registerStaff;
    }

    public void setRegisterStaff(StaffDto registerStaff) {
        this.registerStaff = registerStaff;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public StaffDto getApprovalStaff() {
        return approvalStaff;
    }

    public void setApprovalStaff(StaffDto approvalStaff) {
        this.approvalStaff = approvalStaff;
    }
}
