package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.domain.TimeSheetDetail;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/*
 * Bảng phân công công việc, nhân viên nào sẽ làm việc vào giờ nào
 */
@Table(name = "tbl_staff_work_schedule")
@Entity
public class StaffWorkSchedule extends BaseObject {
    private static final long serialVersionUID = 572369945947940265L;

    @ManyToOne
    @JoinColumn(name = "shift_work_id")
    private ShiftWork shiftWork;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff; // Nhân viên được phân ca làm việc

    @Column(name = "working_date", nullable = true)
    private Date workingDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType; // Loại nghỉ. Chi tiết: HrConstants.LeaveTypeCode

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffWorkScheduleShiftPeriod> leaveScheduleShiftPeriods; // Giai đoạn làm việc xin nghỉ nếu loại nghỉ là nghỉ nửa ngày. Có giá trị khi loại nghỉ có giá trị

    @OneToMany(mappedBy = "staffWorkSchedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimeSheetDetail> timesheetDetails = new HashSet<>(); // Các lần chấm công trong ca

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ot_endorser_id")
    private Staff otEndorser; // Người xác nhận OT cho nhân viên

    @Column(name = "confirmed_ot_hours_before_shift", nullable = true)
    private Double confirmedOTHoursBeforeShift = 0D; // Số giờ làm thêm trước ca làm việc đã được xác nhận

    @Column(name = "confirmed_ot_hours_after_shift")
    private Double confirmedOTHoursAfterShift = 0D; // Số giờ làm thêm sau ca làm việc đã được xác nhận

    @Column(name = "working_status")
    private Integer workingStatus; // Trạng thái đi làm. Chi tiết: HrConstants.StaffWorkScheduleWorkingStatus
//	FULL_ATTENDANCE(1, "Đi làm đủ ca"), // Đi làm đủ
//	PARTIAL_ATTENDANCE(2, "Đi làm thiếu giờ"), // Đi làm thiếu giờ
//  NOT_ATTENDANCE(3, "Không đi làm"); // Không đi làm

    @Column(name = "paid_work_status")
    private Integer paidWorkStatus; // Trạng thái tính công. Chi tiết: HrConstants.PaidWorkStatus
//	UNPAID(1, "Không tính công"), // Không tính công
//	PAID(2, "Có tính công"); // Có tính công

    @Column(name = "working_type", nullable = true)
    private Integer workingType; // Loại làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingType
//	NORMAL_WORK(1, "Làm việc bình thường"), // Làm việc bình thường
//	EXTENDED_OVERTIME(2, "Tăng ca kéo dài"); // Tăng ca kéo dài

    @Column(name = "overtime_hours", nullable = true)
    private Double overtimeHours; // Thời gian đăng ký làm thêm. Có giá trị khi: workingType = EXTENDED_OVERTIME
    // (Tăng ca kéo dài)

    @Column(name = "late_arrival_count")
    private Integer lateArrivalCount = 0; // Số lần đi làm muộn: 0(không muộn), 123(tương ứng với số giai đoạn đi muộn)

    @Column(name = "late_arrival_minutes")
    private Integer lateArrivalMinutes = 0; // Số phút đi muộn

    @Column(name = "early_exit_count")
    private Integer earlyExitCount = 0; // Số lần về sớm

    @Column(name = "early_exit_minutes")
    private Integer earlyExitMinutes = 0; // Số phút về sớm

    @Column(name = "early_arrival_minutes")
    private Integer earlyArrivalMinutes = 0; // Số phút đến sớm

    @Column(name = "late_exit_minutes")
    private Integer lateExitMinutes = 0; // Số phút về muộn

    @Column(name = "allow_one_entry_only")
    private Boolean allowOneEntryOnly; // Chỉ cho phép chấm công ra vào 1 lần trong toàn ca (shiftwork). Cho phép chấm
    // 1 lần => Chỉ tạo ra 1 timesheetdetail

    @Column(name = "timekeeping_calculation_type")
    private Integer timekeepingCalculationType; // Cách tính thời gian chấm công HrConstants.TimekeepingCalculationType

    // Ca làm việc này cần người quản lý phê duyệt kết qủả
    @Column(name = "need_manager_approval")
    private Boolean needManagerApproval;

    // Trường dưới có giá trị khi needManagerApproval = true
    @Column(name = "approval_status")
    private Integer approvalStatus; // Trạng thái phê duyệt kết quả làm việc. Chi tiết:
    // HrConstants.StaffWorkScheduleApprovalStatus

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_id")
    private Staff coordinator; // Người phân ca làm việc


    @Column(name = "estimated_working_hours")
    private Double estimatedWorkingHours = 0D; // Số giờ làm việc dự kiến

    @Column(name = "total_hours", nullable = true)
    private Double totalHours = 0D; // Số giờ thực tế nhân viên đã làm việc của ca này

    @Column(name = "total_valid_hours", nullable = true)
    private Double totalValidHours = 0D; // Số giờ hợp lệ được tính trong ca làm việc

    @Column(name = "converted_working_hours", nullable = true)
    private Double convertedWorkingHours = 0D; // Số giờ công quy đổi của nhân viên

    @Column(name = "paid_leave_hours")
    private Double paidLeaveHours = 0D; // Số giờ nghỉ được tính công

    @Column(name = "unpaid_leave_hours")
    private Double unpaidLeaveHours = 0D; // Số giờ nghỉ không được tính công

    @Column(name = "total_paid_work")
    private Double totalPaidWork = 0.0; // Công được tính của nhân viên. VD: 0.5 ngày công, 1 ngày công. Được tính theo totalHours

    @Column(name = "paid_leave_work_ratio")
    private Double paidLeaveWorkRatio = 0.0; // Công nghỉ phép có tính lương của nhân viên. VD: Nghỉ phép 0.5 ngày công. Tính theo paidLeaveHours

    @Column(name = "unpaid_leave_work_ratio")
    private Double unpaidLeaveWorkRatio = 0.0; // Công nghỉ phép KHÔNG tính lương của nhân viên. VD: Nghỉ KHÔNG phép 0.5 ngày công. Tính theo unpaidLeaveHours

    // Ca làm việc này đã bị khóa hay chưa, nếu đã bị khóa thì không cho phép chấm công hay tính toán lại
    @Column(name = "is_locked")
    private Boolean isLocked;

    @Column(name = "during_pregnancy")
    private Boolean duringPregnancy; // Ca làm việc này được tính đang trong thời kỳ thai sản


    public Boolean getDuringPregnancy() {
        return duringPregnancy;
    }

    public void setDuringPregnancy(Boolean duringPregnancy) {
        this.duringPregnancy = duringPregnancy;
    }

    public Boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	public Double getTotalValidHours() {
        return totalValidHours;
    }

    public void setTotalValidHours(Double totalValidHours) {
        this.totalValidHours = totalValidHours;
    }

    public Double getPaidLeaveWorkRatio() {
        return paidLeaveWorkRatio;
    }

    public void setPaidLeaveWorkRatio(Double paidLeaveWorkRatio) {
        this.paidLeaveWorkRatio = paidLeaveWorkRatio;
    }

    public Double getUnpaidLeaveWorkRatio() {
        return unpaidLeaveWorkRatio;
    }

    public void setUnpaidLeaveWorkRatio(Double unpaidLeaveWorkRatio) {
        this.unpaidLeaveWorkRatio = unpaidLeaveWorkRatio;
    }

    public Double getEstimatedWorkingHours() {
        return estimatedWorkingHours;
    }

    public void setEstimatedWorkingHours(Double estimatedWorkingHours) {
        this.estimatedWorkingHours = estimatedWorkingHours;
    }

    public Double getPaidLeaveHours() {
        return paidLeaveHours;
    }

    public void setPaidLeaveHours(Double paidLeaveHours) {
        this.paidLeaveHours = paidLeaveHours;
    }

    public Double getUnpaidLeaveHours() {
        return unpaidLeaveHours;
    }

    public void setUnpaidLeaveHours(Double unpaidLeaveHours) {
        this.unpaidLeaveHours = unpaidLeaveHours;
    }

    public Boolean getNeedManagerApproval() {
        return needManagerApproval;
    }

    public void setNeedManagerApproval(Boolean needManagerApproval) {
        this.needManagerApproval = needManagerApproval;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public ShiftWork getShiftWork() {
        return shiftWork;
    }

    public void setShiftWork(ShiftWork shiftWork) {
        this.shiftWork = shiftWork;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public Integer getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(Integer workingStatus) {
        this.workingStatus = workingStatus;
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

    public Set<TimeSheetDetail> getTimesheetDetails() {
        return timesheetDetails;
    }

    public void setTimesheetDetails(Set<TimeSheetDetail> timesheetDetails) {
        this.timesheetDetails = timesheetDetails;
    }

    public Integer getPaidWorkStatus() {
        return paidWorkStatus;
    }

    // Thêm getter và setter
    public Integer getTimekeepingCalculationType() {
        return timekeepingCalculationType;
    }

    public void setTimekeepingCalculationType(Integer timekeepingCalculationType) {
        this.timekeepingCalculationType = timekeepingCalculationType;
    }

    public void setPaidWorkStatus(Integer paidWorkStatus) {
        this.paidWorkStatus = paidWorkStatus;
    }

    public Double getTotalPaidWork() {
        return totalPaidWork;
    }

    public void setTotalPaidWork(Double totalPaidWork) {
        this.totalPaidWork = totalPaidWork;
    }

    public Integer getLateArrivalCount() {
        return lateArrivalCount;
    }

    public void setLateArrivalCount(Integer lateArrivalCount) {
        this.lateArrivalCount = lateArrivalCount;
    }

    public Integer getLateArrivalMinutes() {
        return lateArrivalMinutes;
    }

    public void setLateArrivalMinutes(Integer lateArrivalMinutes) {
        this.lateArrivalMinutes = lateArrivalMinutes;
    }

    public Integer getEarlyExitCount() {
        return earlyExitCount;
    }

    public void setEarlyExitCount(Integer earlyExitCount) {
        this.earlyExitCount = earlyExitCount;
    }

    public Integer getEarlyExitMinutes() {
        return earlyExitMinutes;
    }

    public void setEarlyExitMinutes(Integer earlyExitMinutes) {
        this.earlyExitMinutes = earlyExitMinutes;
    }

    public Integer getEarlyArrivalMinutes() {
        return earlyArrivalMinutes;
    }

    public void setEarlyArrivalMinutes(Integer earlyArrivalMinutes) {
        this.earlyArrivalMinutes = earlyArrivalMinutes;
    }

    public Integer getLateExitMinutes() {
        return lateExitMinutes;
    }

    public void setLateExitMinutes(Integer lateExitMinutes) {
        this.lateExitMinutes = lateExitMinutes;
    }

    public Boolean getAllowOneEntryOnly() {
        return allowOneEntryOnly;
    }

    public void setAllowOneEntryOnly(Boolean allowOneEntryOnly) {
        this.allowOneEntryOnly = allowOneEntryOnly;
    }

    public Double getConfirmedOTHoursBeforeShift() {
        return confirmedOTHoursBeforeShift;
    }

    public void setConfirmedOTHoursBeforeShift(Double confirmedOTHoursBeforeShift) {
        this.confirmedOTHoursBeforeShift = confirmedOTHoursBeforeShift;
    }

    public Double getConfirmedOTHoursAfterShift() {
        return confirmedOTHoursAfterShift;
    }

    public void setConfirmedOTHoursAfterShift(Double confirmedOTHoursAfterShift) {
        this.confirmedOTHoursAfterShift = confirmedOTHoursAfterShift;
    }

    public Staff getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Staff coordinator) {
        this.coordinator = coordinator;
    }

    public Staff getOtEndorser() {
        return otEndorser;
    }

    public void setOtEndorser(Staff otEndorser) {
        this.otEndorser = otEndorser;
    }

    public Double getConvertedWorkingHours() {
        return convertedWorkingHours;
    }

    public void setConvertedWorkingHours(Double convertedWorkingHours) {
        this.convertedWorkingHours = convertedWorkingHours;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Set<StaffWorkScheduleShiftPeriod> getLeaveScheduleShiftPeriods() {
        return leaveScheduleShiftPeriods;
    }

    public void setLeaveScheduleShiftPeriods(Set<StaffWorkScheduleShiftPeriod> leaveScheduleShiftPeriods) {
        this.leaveScheduleShiftPeriods = leaveScheduleShiftPeriods;
    }
}
