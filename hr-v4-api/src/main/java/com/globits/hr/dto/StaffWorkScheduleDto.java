package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.domain.StaffWorkScheduleShiftPeriod;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.LeaveTypeDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.*;
import java.util.stream.Collectors;

//@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class StaffWorkScheduleDto {
    private UUID id;
    private UUID staffId;
    private String staffCode;
    private StaffDto staff;
    private String shiftWorkCode;
    private ShiftWorkDto shiftWork;
    private Date workingDate;
    private Double overtimeHours; // Thời gian đăng ký làm thêm. Có giá trị khi: workingType = EXTENDED_OVERTIME
    // (Tăng ca kéo dài)
    private Integer workingType; // Loại làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingType
    private Integer workingStatus; // Trạng thái làm việc của nhân viên đối với ca làm việc. Chi tiết:
    // HrConstants.StaffWorkScheduleWorkingStatus
    private Integer paidWorkStatus; // Trạng thái tính công. Chi tiết: HrConstants.PaidWorkStatus
    private Boolean allowOneEntryOnly;
    private StaffDto otEndorser; // Người xác nhận OT cho nhân viên
    private Double confirmedOTHoursBeforeShift; // Số giờ làm thêm trước ca làm việc đã được xác nhận
    private Double confirmedOTHoursAfterShift; // Số giờ làm thêm sau ca làm việc đã được xác nhận
    private StaffDto coordinator; // Người phân ca làm việc
    private LeaveTypeDto leaveType; // Loại nghỉ. Có giá trị khi workingStatus = NOT_ATTENDANCE (không đi làm)

    // Chỉ số được thống kê
    private Integer lateArrivalCount; // Số lần đi làm muộn
    private Integer lateArrivalMinutes; // Số phút đi muộn
    private Integer earlyExitCount; // Số lần về sớm
    private Integer earlyExitMinutes; // Số phút về sớm
    private Integer earlyArrivalMinutes; // Số phút đến sớm
    private Integer lateExitMinutes; // Số phút về muộn
    private Double totalPaidWork; // công được tính của nhân viên. VD: 0.5 ngày công, 1 ngày công

    private Double estimatedWorkingHours = 0D; // Số giờ làm việc dự kiến
    private Double totalHours = 0D; // Số giờ thực tế nhân viên đã làm việc của ca này
    private Double totalValidHours = 0D; // Số giờ hợp lệ được tính trong ca làm việc
    private Double convertedWorkingHours = 0D; // Số giờ công quy đổi của nhân viên
    private Double paidLeaveHours = 0D; // Số giờ nghỉ được tính công
    private Double unpaidLeaveHours = 0D; // Số giờ nghỉ không được tính công
    private Double paidLeaveWorkRatio = 0.0; // Công nghỉ phép có tính lương của nhân viên. VD: Nghỉ phép 0.5 ngày công.
    // Tính theo paidLeaveHours
    private Double unpaidLeaveWorkRatio = 0.0; // Công nghỉ phép KHÔNG tính lương của nhân viên. VD: Nghỉ KHÔNG phép 0.5
    // ngày công. Tính theo unpaidLeaveHours

    private Date firstCheckIn; // Lần checkin đầu tiên (được lấy từ timesheetDetail)
    private Date lastCheckout; // Lần checkout cuối cùng (được lấy từ timesheetDetail)

    private List<TimeSheetDetailDto> timeSheetDetails; // Lịch sử chấm công
    private List<ShiftWorkTimePeriodDto> leavePeriods;
    private ShiftWorkTimePeriodDto leavePeriod;

    // Cách tính thời gian chấm công HrConstants.TimekeepingCalculationType
    private Integer timekeepingCalculationType;
    // Ca làm việc này cần người quản lý phê duyệt
    private Boolean needManagerApproval;
    // Trường dưới có giá trị khi needManagerApproval = true
    private Integer approvalStatus; // Trạng thái phê duyệt kết quả làm việc. Chi tiết:
    // HrConstants.StaffWorkScheduleApprovalStatus

    // Ca làm việc này đã bị khóa hay chưa, nếu đã bị khóa thì không cho phép chấm công hay tính toán lại
    private Boolean isLocked;
    private Boolean duringPregnancy; // Ca làm việc này được tính đang trong thời kỳ thai sản

    private String errorMessage; // Thông báo lỗi khi import từ file excel

    private List<ShiftWorkDto> shiftWorks;

    public StaffWorkScheduleDto() {
    }

    public StaffWorkScheduleDto(StaffWorkSchedule entity) {
        if (entity == null) return;
        this.id = entity.getId();
        this.workingDate = entity.getWorkingDate();
        this.workingStatus = entity.getWorkingStatus();
        this.workingType = entity.getWorkingType();
        this.overtimeHours = entity.getOvertimeHours();
        this.paidWorkStatus = entity.getPaidWorkStatus();
        this.approvalStatus = entity.getApprovalStatus();
        this.needManagerApproval = entity.getNeedManagerApproval();
        this.unpaidLeaveWorkRatio = entity.getUnpaidLeaveWorkRatio();
        this.paidLeaveWorkRatio = entity.getPaidLeaveWorkRatio();
        this.isLocked = entity.getIsLocked();
        this.duringPregnancy = entity.getDuringPregnancy();

        if (entity.getStaff() != null) {
            this.staff = new StaffDto();
            this.staffId = entity.getStaff().getId();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setSkipLateEarlyCount(entity.getStaff().getSkipLateEarlyCount());
            this.staff.setSkipOvertimeCount(entity.getStaff().getSkipOvertimeCount());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
            this.staff.setMainPosition(entity.getStaff().getCurrentPositions());
            this.staffCode = entity.getStaff().getStaffCode();
            if (entity.getStaff().getCurrentPositions() != null && !entity.getStaff().getCurrentPositions().isEmpty()) {
                for (Position position : entity.getStaff().getCurrentPositions()) {
                    if (position.getIsMain() != null && position.getIsMain()) {
                        this.staff.setPositionTitle(new PositionTitleDto(position.getTitle(), false));
                        break;
                    }
                }

            }
        }

        if (entity.getShiftWork() != null) {
            this.shiftWork = new ShiftWorkDto(entity.getShiftWork());
            this.shiftWorkCode = entity.getShiftWork().getCode();
        }

        this.lateArrivalCount = entity.getLateArrivalCount();
        this.lateArrivalMinutes = entity.getLateArrivalMinutes();
        this.earlyExitCount = entity.getEarlyExitCount();
        this.earlyExitMinutes = entity.getEarlyExitMinutes();
        this.earlyArrivalMinutes = entity.getEarlyArrivalMinutes();
        this.lateExitMinutes = entity.getLateExitMinutes();
        this.totalPaidWork = entity.getTotalPaidWork();
        this.allowOneEntryOnly = entity.getAllowOneEntryOnly();
        this.timekeepingCalculationType = entity.getTimekeepingCalculationType();

        // Số giờ được thống kê
        this.estimatedWorkingHours = entity.getEstimatedWorkingHours(); // Số giờ làm việc dự kiến
        this.totalHours = entity.getTotalHours(); // Số giờ thực tế nhân viên đã làm việc của ca này
        this.totalValidHours = entity.getTotalValidHours(); // Số giờ hợp lệ được tính trong ca làm việc
        this.convertedWorkingHours = entity.getConvertedWorkingHours(); // Số giờ công quy đổi của nhân viên
        this.paidLeaveHours = entity.getPaidLeaveHours(); // Số giờ nghỉ được tính công
        this.unpaidLeaveHours = entity.getUnpaidLeaveHours(); // Số giờ nghỉ không được tính công

        if (entity.getLeaveType() != null) {
            this.leaveType = new LeaveTypeDto(entity.getLeaveType());
        }

        if (entity.getOtEndorser() != null) {
            this.otEndorser = new StaffDto();
            this.otEndorser.setId(entity.getOtEndorser().getId());
            this.otEndorser.setStaffCode(entity.getOtEndorser().getStaffCode());
            this.otEndorser.setDisplayName(entity.getOtEndorser().getDisplayName());
        }

        if (entity.getCoordinator() != null) {
            this.coordinator = new StaffDto();
            this.coordinator.setId(entity.getCoordinator().getId());
            this.coordinator.setStaffCode(entity.getCoordinator().getStaffCode());
            this.coordinator.setDisplayName(entity.getCoordinator().getDisplayName());
        }

        this.confirmedOTHoursBeforeShift = entity.getConfirmedOTHoursBeforeShift(); // Số giờ làm thêm trước ca làm việc
        // đã được xác nhận
        this.confirmedOTHoursAfterShift = entity.getConfirmedOTHoursAfterShift();

        if (entity.getLeaveScheduleShiftPeriods() != null && !entity.getLeaveScheduleShiftPeriods().isEmpty()) {
            this.leavePeriods = new ArrayList<ShiftWorkTimePeriodDto>();

            for (StaffWorkScheduleShiftPeriod scheduleLeavePeriod : entity.getLeaveScheduleShiftPeriods()) {
                if (scheduleLeavePeriod == null || scheduleLeavePeriod.getLeavePeriod() == null)
                    continue;

                ShiftWorkTimePeriodDto leavePeriod = new ShiftWorkTimePeriodDto(scheduleLeavePeriod.getLeavePeriod());

                leavePeriods.add(leavePeriod);
            }
        }

        if (this.leavePeriods != null && !this.leavePeriods.isEmpty()) {
            leavePeriod = this.getLeavePeriods().get(0);
        }

        if (entity.getTimesheetDetails() == null || entity.getTimesheetDetails().isEmpty())
            return;

        List<TimeSheetDetailDto> listDetails = entity.getTimesheetDetails().stream()
                .map(detail -> new TimeSheetDetailDto(detail, false)).collect(Collectors.toList());
        Collections.sort(listDetails, new Comparator<TimeSheetDetailDto>() {
            @Override
            public int compare(TimeSheetDetailDto o1, TimeSheetDetailDto o2) {
                // First, compare by StartTime
                if (o1.getStartTime() == null && o2.getStartTime() == null)
                    return 0;
                if (o1.getStartTime() == null)
                    return 1;
                if (o2.getStartTime() == null)
                    return -1;

                int startTimeComparison = o1.getStartTime().compareTo(o2.getStartTime());
                if (startTimeComparison != 0) {
                    return startTimeComparison;
                }

                // If StartTime is the same, compare by endTime (handling nulls)
                if (o1.getEndTime() == null && o2.getEndTime() == null)
                    return 0;
                if (o1.getEndTime() == null)
                    return 1;
                if (o2.getEndTime() == null)
                    return -1;

                return o1.getEndTime().compareTo(o2.getEndTime());
            }
        });

        this.timeSheetDetails = listDetails;


        // Chỉ chấm công ra vào 1 lần
        if (entity.getTimesheetDetails() != null && !entity.getTimesheetDetails().isEmpty()) {
            if (entity.getTimesheetDetails().size() == 1) {
                TimeSheetDetail timekeeping = new ArrayList<>(entity.getTimesheetDetails()).get(0);

                this.firstCheckIn = timekeeping.getStartTime();
                this.lastCheckout = timekeeping.getEndTime();
            }
            // Chấm công ra vào nhiều lần
            else {
                List<TimeSheetDetail> listTimeSheetDetail = new ArrayList<>(entity.getTimesheetDetails());

                Collections.sort(listTimeSheetDetail, new Comparator<TimeSheetDetail>() {
                    @Override
                    public int compare(TimeSheetDetail o1, TimeSheetDetail o2) {
                        // First, compare by StartTime
                        if (o1.getStartTime() == null && o2.getStartTime() == null)
                            return 0;
                        if (o1.getStartTime() == null)
                            return 1;
                        if (o2.getStartTime() == null)
                            return -1;

                        int startTimeComparison = o1.getStartTime().compareTo(o2.getStartTime());
                        if (startTimeComparison != 0) {
                            return startTimeComparison;
                        }

                        // If StartTime is the same, compare by endTime (handling nulls)
                        if (o1.getEndTime() == null && o2.getEndTime() == null)
                            return 0;
                        if (o1.getEndTime() == null)
                            return 1;
                        if (o2.getEndTime() == null)
                            return -1;

                        return o1.getEndTime().compareTo(o2.getEndTime());
                    }
                });
                this.firstCheckIn = listDetails.get(0).getStartTime();
                this.lastCheckout = listDetails.get(listDetails.size() - 1).getEndTime();
            }
        }

    }

    public List<ShiftWorkDto> getShiftWorks() {
        return shiftWorks;
    }

    public void setShiftWorks(List<ShiftWorkDto> shiftWorks) {
        this.shiftWorks = shiftWorks;
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

    public Integer getTimekeepingCalculationType() {
        return timekeepingCalculationType;
    }

    public void setTimekeepingCalculationType(Integer timekeepingCalculationType) {
        this.timekeepingCalculationType = timekeepingCalculationType;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public List<TimeSheetDetailDto> getTimeSheetDetails() {
        return timeSheetDetails;
    }

    public void setTimeSheetDetails(List<TimeSheetDetailDto> timeSheetDetails) {
        this.timeSheetDetails = timeSheetDetails;
    }

    public Integer getWorkingType() {
        return workingType;
    }

    public void setWorkingType(Integer workingType) {
        this.workingType = workingType;
    }

    public Integer getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(Integer workingStatus) {
        this.workingStatus = workingStatus;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
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

    public Integer getPaidWorkStatus() {
        return paidWorkStatus;
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

    public Double getConvertedWorkingHours() {
        return convertedWorkingHours;
    }

    public void setConvertedWorkingHours(Double convertedWorkingHours) {
        this.convertedWorkingHours = convertedWorkingHours;
    }

    public LeaveTypeDto getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveTypeDto leaveType) {
        this.leaveType = leaveType;
    }

    public String getShiftWorkCode() {
        return shiftWorkCode;
    }

    public void setShiftWorkCode(String shiftWorkCode) {
        this.shiftWorkCode = shiftWorkCode;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public List<ShiftWorkTimePeriodDto> getLeavePeriods() {
        return leavePeriods;
    }

    public void setLeavePeriods(List<ShiftWorkTimePeriodDto> leavePeriods) {
        this.leavePeriods = leavePeriods;
    }

    public ShiftWorkTimePeriodDto getLeavePeriod() {
        return leavePeriod;
    }

    public void setLeavePeriod(ShiftWorkTimePeriodDto leavePeriod) {
        this.leavePeriod = leavePeriod;
    }

    public Boolean getDuringPregnancy() {
        return duringPregnancy;
    }

    public void setDuringPregnancy(Boolean duringPregnancy) {
        this.duringPregnancy = duringPregnancy;
    }

	public UUID getStaffId() {
		return staffId;
	}

	public void setStaffId(UUID staffId) {
		this.staffId = staffId;
	}
    
    
}
