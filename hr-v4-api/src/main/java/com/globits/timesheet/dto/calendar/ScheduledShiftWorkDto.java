package com.globits.timesheet.dto.calendar;

import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.LeaveTypeDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ScheduledShiftWorkDto {
    // Các trường của ca làm việc
    private UUID id; // id ca làm việc
    private String code; // mã ca làm việc
    private String name; // tên ca làm việc
    private Double totalHours; // tổng số giờ làm của ca làm việc
    private UUID staffWorkScheduleId; // chỉ dùng trong màn hình bảng phân công
    private Integer shiftWorkType; // Loại ca làm việc
    private List<ScheduledShiftWorkTimePeriodDto> timePeriods; // Các giai đoạn làm việc trong ca
    private List<ScheduledTimesheetDetailDto> timeSheetDetails; // Các lần chấm công của ca làm việc này

    // Các trường sau lấy ở staffWorkSchedule
    private Double staffTotalHours; // Số giờ nhân viên đã làm việc của ca này
    private Double convertedWorkingHours; // Số giờ công quy đổi của nhân viên
    private Integer workingType; // Loại làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingType
    private Integer workingStatus; // Trạng thái làm việc của nhân viên đối với ca làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingStatus
    private Integer paidWorkStatus; // Trạng thái tính công. Chi tiết: HrConstants.PaidWorkStatus
    private Integer lateArrivalCount; // Số lần đi làm muộn
    private Integer lateArrivalMinutes; // Số phút đi muộn
    private Integer earlyExitCount; // Số lần về sớm
    private Integer earlyExitMinutes; // Số phút về sớm
    private Integer earlyArrivalMinutes; // Số phút đến sớm
    private Integer lateExitMinutes; // Số phút về muộn
    private Double totalPaidWork; // công được tính của nhân viên. VD: 0.5 ngày công, 1 ngày công
    private Boolean allowOneEntryOnly;
    private StaffDto otEndorser; // Người xác nhận OT cho nhân viên
    private Double confirmedOTHoursBeforeShift; // Số giờ làm thêm trước ca làm việc đã được xác nhận
    private Double confirmedOTHoursAfterShift; // Số giờ làm thêm sau ca làm việc đã được xác nhận
    private StaffDto coordinator; // Người phân ca làm việc

    private Date firstCheckIn; // Lần checkin đầu tiên (được lấy từ timesheetDetail)
    private Date lastCheckout; // Lần checkout cuối cùng (được lấy từ timesheetDetail)

    private Boolean needManagerApproval;// Ca làm việc này cần người quản lý phê duyệt
    private Integer approvalStatus;   // Trạng thái phê duyệt kết quả làm việc. Chi tiết: HrConstants.StaffWorkScheduleApprovalStatus
    private LeaveTypeDto leaveType; // Loại nghỉ. Có giá trị khi workingStatus = NOT_ATTENDANCE (không đi làm)

    public ScheduledShiftWorkDto() {
        this.timePeriods = new ArrayList<>();
    }

    public ScheduledShiftWorkDto(StaffWorkSchedule staffWorkSchedule) {
        if (staffWorkSchedule == null) return;
        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();

        this.staffWorkScheduleId = staffWorkSchedule.getId();
        this.needManagerApproval = staffWorkSchedule.getNeedManagerApproval();
        this.approvalStatus = staffWorkSchedule.getApprovalStatus();
        this.id = shiftWork.getId();
        this.code = shiftWork.getCode();
        this.name = shiftWork.getName();
        this.totalHours = shiftWork.getTotalHours();
        this.totalPaidWork = staffWorkSchedule.getTotalPaidWork();

        if (shiftWork.getTimePeriods() != null && !shiftWork.getTimePeriods().isEmpty()) {
            this.timePeriods = new ArrayList<>();

            for (ShiftWorkTimePeriod timePeriodEntity : shiftWork.getTimePeriods()) {
                this.timePeriods.add(new ScheduledShiftWorkTimePeriodDto(timePeriodEntity));
            }
        }
        this.workingStatus = staffWorkSchedule.getWorkingStatus();

        if (staffWorkSchedule.getLeaveType() != null) {
            this.leaveType = new LeaveTypeDto(staffWorkSchedule.getLeaveType(), false);
        }

        this.shiftWorkType = shiftWork.getShiftWorkType();
        this.timeSheetDetails = new ArrayList<>();
        if (staffWorkSchedule.getTimesheetDetails() != null && !staffWorkSchedule.getTimesheetDetails().isEmpty()) {
            for (TimeSheetDetail tsd : staffWorkSchedule.getTimesheetDetails()) {
                this.timeSheetDetails.add(new ScheduledTimesheetDetailDto(tsd));
            }
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public List<ScheduledShiftWorkTimePeriodDto> getTimePeriods() {
        return timePeriods;
    }

    public void setTimePeriods(List<ScheduledShiftWorkTimePeriodDto> timePeriods) {
        this.timePeriods = timePeriods;
    }

    public UUID getStaffWorkScheduleId() {
        return staffWorkScheduleId;
    }

    public void setStaffWorkScheduleId(UUID staffWorkScheduleId) {
        this.staffWorkScheduleId = staffWorkScheduleId;
    }

    public List<ScheduledTimesheetDetailDto> getTimeSheetDetails() {
        return timeSheetDetails;
    }

    public void setTimeSheetDetails(List<ScheduledTimesheetDetailDto> timeSheetDetails) {
        this.timeSheetDetails = timeSheetDetails;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(Integer workingStatus) {
        this.workingStatus = workingStatus;
    }

    public Integer getShiftWorkType() {
        return shiftWorkType;
    }

    public void setShiftWorkType(Integer shiftWorkType) {
        this.shiftWorkType = shiftWorkType;
    }

    public Double getStaffTotalHours() {
        return staffTotalHours;
    }

    public void setStaffTotalHours(Double staffTotalHours) {
        this.staffTotalHours = staffTotalHours;
    }

    public Double getConvertedWorkingHours() {
        return convertedWorkingHours;
    }

    public void setConvertedWorkingHours(Double convertedWorkingHours) {
        this.convertedWorkingHours = convertedWorkingHours;
    }

    public Integer getWorkingType() {
        return workingType;
    }

    public void setWorkingType(Integer workingType) {
        this.workingType = workingType;
    }

    public Integer getPaidWorkStatus() {
        return paidWorkStatus;
    }

    public void setPaidWorkStatus(Integer paidWorkStatus) {
        this.paidWorkStatus = paidWorkStatus;
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

    public Double getTotalPaidWork() {
        return totalPaidWork;
    }

    public void setTotalPaidWork(Double totalPaidWork) {
        this.totalPaidWork = totalPaidWork;
    }

    public Boolean getAllowOneEntryOnly() {
        return allowOneEntryOnly;
    }

    public void setAllowOneEntryOnly(Boolean allowOneEntryOnly) {
        this.allowOneEntryOnly = allowOneEntryOnly;
    }

    public StaffDto getOtEndorser() {
        return otEndorser;
    }

    public void setOtEndorser(StaffDto otEndorser) {
        this.otEndorser = otEndorser;
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

    public StaffDto getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(StaffDto coordinator) {
        this.coordinator = coordinator;
    }

    public Date getFirstCheckIn() {
        return firstCheckIn;
    }

    public void setFirstCheckIn(Date firstCheckIn) {
        this.firstCheckIn = firstCheckIn;
    }

    public Date getLastCheckout() {
        return lastCheckout;
    }

    public void setLastCheckout(Date lastCheckout) {
        this.lastCheckout = lastCheckout;
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

    public LeaveTypeDto getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveTypeDto leaveType) {
        this.leaveType = leaveType;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
