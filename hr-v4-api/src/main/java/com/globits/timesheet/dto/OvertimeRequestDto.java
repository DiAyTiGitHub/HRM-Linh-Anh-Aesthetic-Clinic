package com.globits.timesheet.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.timesheet.domain.OvertimeRequest;

public class OvertimeRequestDto extends BaseObjectDto {
    private StaffDto staff;
    private StaffWorkScheduleDto staffWorkSchedule; // yêu cầu phê duyệt OT ngày làm việc
    //private Date requestDate; // ngày tạo yêu cầu 
    private Double requestOTHoursBeforeShift;  // Số giờ làm thêm trước ca làm việc được yêu cầu tính OT
    private Double requestOTHoursAfterShift; // Số giờ làm thêm sau ca làm việc được yêu cầu tính OT
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.OvertimeRequestApprovalStatus
    //    NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
    //    APPROVED(2, "Đã duyệt"), // Đã duyệt
    //    NOT_APPROVED(3, "Không duyệt");


    public OvertimeRequestDto() {

    }

    public OvertimeRequestDto(OvertimeRequest entity, Boolean isGetFull) {
        super(entity);
        if (entity == null) return;
        //this.requestDate = entity.getRequestDate();
        this.requestOTHoursBeforeShift = entity.getRequestOTHoursBeforeShift();
        this.requestOTHoursAfterShift = entity.getRequestOTHoursAfterShift();
        this.approvalStatus = entity.getApprovalStatus();

        if (isGetFull) {
            if (entity.getStaffWorkSchedule() != null) {
                this.staffWorkSchedule = new StaffWorkScheduleDto(entity.getStaffWorkSchedule());
                if (entity.getStaffWorkSchedule().getStaff() != null) {
                    this.staff = new StaffDto(entity.getStaffWorkSchedule().getStaff(), false);
                }
            }
        }
    }

    public OvertimeRequestDto(OvertimeRequest entity) {
        this(entity, true);
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public StaffWorkScheduleDto getStaffWorkSchedule() {
        return staffWorkSchedule;
    }

    public void setStaffWorkSchedule(StaffWorkScheduleDto staffWorkSchedule) {
        this.staffWorkSchedule = staffWorkSchedule;
    }

    public Double getRequestOTHoursBeforeShift() {
        return requestOTHoursBeforeShift;
    }

    public void setRequestOTHoursBeforeShift(Double requestOTHoursBeforeShift) {
        this.requestOTHoursBeforeShift = requestOTHoursBeforeShift;
    }

    public Double getRequestOTHoursAfterShift() {
        return requestOTHoursAfterShift;
    }

    public void setRequestOTHoursAfterShift(Double requestOTHoursAfterShift) {
        this.requestOTHoursAfterShift = requestOTHoursAfterShift;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

}
