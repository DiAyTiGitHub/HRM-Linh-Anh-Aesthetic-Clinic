package com.globits.timesheet.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import jakarta.persistence.*;

// Yêu cầu xác nhận làm thêm giờ
@Table(name = "tbl_overtime_request")
@Entity
public class OvertimeRequest extends BaseObject {

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private StaffWorkSchedule staffWorkSchedule; // Yêu cầu xác nhận cho ca làm việc nào

    @Column(name = "request_ot_hours_before_shift", nullable = true)
    private Double requestOTHoursBeforeShift; // Số giờ làm thêm trước ca làm việc được yêu cầu tính OT

    @Column(name = "request_ot_hours_after_shift")
    private Double requestOTHoursAfterShift; // Số giờ làm thêm sau ca làm việc được yêu cầu tính OT

    @Column(name = "approval_status", nullable = true)
    private Integer approvalStatus; // Trạng thái phê duyệt yêu cầu xác nhận giờ làm thêm. Chi tiết: HrConstants.OvertimeRequestApprovalStatus

    public StaffWorkSchedule getStaffWorkSchedule() {
        return staffWorkSchedule;
    }

    public void setStaffWorkSchedule(StaffWorkSchedule staffWorkSchedule) {
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
