package com.globits.timesheet.dto.calendar;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.dto.IndexLeaveTypeDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ScheduledStaffCalendarDto {

    private Integer displayOrder; // Thứ tự hiển thị = Cột STT trong bảng báo cáo chấm công
    // staff
    private StaffDto staff;
    private UUID staffId; // Mã NV
    private String displayName; // Họ tên NV
    private String staffType; // Loại nhân viên
    private String staffCode; // Mã nhân viên
    private Date birthDate; // Ngày sinh

    // position
    private String currentPosition; // Chức vụ chính
    private String currentPositionTitle;
    private String currentDepartment; // Phòng ban chính
    private String currentOrganization; // Đơn vị chính

    private String codeCurrentPositionTitle;
    private String codeCurrentPosition;
    private String codeCurrentDepartment;
    private String codeCurrentOrganization;

    // staffWorkScheduled
    private List<ScheduledStaffItemDto> workingSchedules;


    // HrConstants.StaffWorkScheduleWorkingStatus
    private Integer totalAssignedShifts; //  Số ca làm việc được phân
    private Integer totalFullAttendanceShifts; // Số ca làm việc nhân viên hoàn thành/đi làm đủ
    private Integer totalPartialAttendanceShifts; //  Số ca làm việc nhân viên đi làm thiếu giờ
    private Integer totalNotAttendenceShifts; // Số ca không đi làm

//    private Integer totalAuthorizedLeaveShifts; //  Số ca làm việc nhân viên nghỉ có phép
//    private Integer totalUnauthorizedLeaveShifts; //  Số ca làm việc nhân viên nghỉ không phép
//    private Integer totalAuthorizedPaidLeaveShifts; //  Số ca làm việc nhân viên nghỉ có phép HƯỞNG LƯƠNG
//    private Integer totalAuthorizedUnPaidLeaveShifts; //  Số ca làm việc nhân viên nghỉ có phép KHÔNG HƯỞNG LƯƠNG

    // Các trường sau được cộng tổng với dữ liệu từ List<ScheduledStaffItemDto> workingSchedules bên trên
    private Double totalAssignedHours; // Số giờ làm việc được phân
    private Double totalWorkedHours; // Số giờ làm việc thực tế

    private Double convertedWorkingHours; // tổng Số giờ công quy đổi của nhân viên
    private Integer lateArrivalCount; // tổng Số lần đi làm muộn
    private Integer lateArrivalMinutes; //tổng  Số phút đi muộn
    private Integer earlyExitCount; //tổng Số lần về sớm
    private Integer earlyExitMinutes; // tổng Số phút về sớm
    private Integer earlyArrivalMinutes; // tổng Số phút đến sớm
    private Integer lateExitMinutes; // tổng Số phút về muộn
    private Double totalPaidWork; // tổng công được tính của nhân viên. VD: 0.5 ngày công, 1 ngày công
    private Double confirmedOTHoursBeforeShift; //tổng Số giờ làm thêm trước ca làm việc đã được xác nhận
    private Double confirmedOTHoursAfterShift; //tổng  Số giờ làm thêm sau ca làm việc đã được xác nhận

    private Double totalPaidLeaveWorkRatio;
    private Double totalUnpaidLeaveWorkRatio;

    private List<IndexLeaveTypeDto> shiftLeaveTypes; // Các loại nghỉ 

    public ScheduledStaffCalendarDto() {

    }

    public ScheduledStaffCalendarDto(Staff staff) {
        this.setStaffId(staff.getId());
        this.setStaffCode(staff.getStaffCode());
        this.setDisplayName(staff.getDisplayName());
        this.setBirthDate(staff.getBirthDate());
        this.staff = new StaffDto(staff, false, false);
        if (staff.getStaffType() != null) {
            this.setStaffType(staff.getStaffType().getName());
        }

        this.workingSchedules = new ArrayList<>();
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<ScheduledStaffItemDto> getWorkingSchedules() {
        return workingSchedules;
    }

    public void setWorkingSchedules(List<ScheduledStaffItemDto> workingSchedules) {
        this.workingSchedules = workingSchedules;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getCurrentDepartment() {
        return currentDepartment;
    }

    public void setCurrentDepartment(String currentDepartment) {
        this.currentDepartment = currentDepartment;
    }

    public String getCurrentOrganization() {
        return currentOrganization;
    }

    public void setCurrentOrganization(String currentOrganization) {
        this.currentOrganization = currentOrganization;
    }

    public Integer getTotalAssignedShifts() {
        return totalAssignedShifts;
    }

    public void setTotalAssignedShifts(Integer totalAssignedShifts) {
        this.totalAssignedShifts = totalAssignedShifts;
    }

    public Double getTotalAssignedHours() {
        return totalAssignedHours;
    }

    public void setTotalAssignedHours(Double totalAssignedHours) {
        this.totalAssignedHours = totalAssignedHours;
    }

    public Double getTotalWorkedHours() {
        return totalWorkedHours;
    }

    public void setTotalWorkedHours(Double totalWorkedHours) {
        this.totalWorkedHours = totalWorkedHours;
    }

    public Integer getTotalFullAttendanceShifts() {
        return totalFullAttendanceShifts;
    }

    public void setTotalFullAttendanceShifts(Integer totalFullAttendanceShifts) {
        this.totalFullAttendanceShifts = totalFullAttendanceShifts;
    }

    public Double getConvertedWorkingHours() {
        return convertedWorkingHours;
    }

    public void setConvertedWorkingHours(Double convertedWorkingHours) {
        this.convertedWorkingHours = convertedWorkingHours;
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

    public String getCurrentPositionTitle() {
        return currentPositionTitle;
    }

    public void setCurrentPositionTitle(String currentPositionTitle) {
        this.currentPositionTitle = currentPositionTitle;
    }

    public String getCodeCurrentPositionTitle() {
        return codeCurrentPositionTitle;
    }

    public void setCodeCurrentPositionTitle(String codeCurrentPositionTitle) {
        this.codeCurrentPositionTitle = codeCurrentPositionTitle;
    }

    public String getCodeCurrentDepartment() {
        return codeCurrentDepartment;
    }

    public void setCodeCurrentDepartment(String codeCurrentDepartment) {
        this.codeCurrentDepartment = codeCurrentDepartment;
    }

    public String getCodeCurrentOrganization() {
        return codeCurrentOrganization;
    }

    public void setCodeCurrentOrganization(String codeCurrentOrganization) {
        this.codeCurrentOrganization = codeCurrentOrganization;
    }

    public String getCodeCurrentPosition() {
        return codeCurrentPosition;
    }

    public void setCodeCurrentPosition(String codeCurrentPosition) {
        this.codeCurrentPosition = codeCurrentPosition;
    }

    public Integer getTotalNotAttendenceShifts() {
        return totalNotAttendenceShifts;
    }

    public void setTotalNotAttendenceShifts(Integer totalNotAttendenceShifts) {
        this.totalNotAttendenceShifts = totalNotAttendenceShifts;
    }

    public Integer getTotalPartialAttendanceShifts() {
        return totalPartialAttendanceShifts;
    }

    public void setTotalPartialAttendanceShifts(Integer totalPartialAttendanceShifts) {
        this.totalPartialAttendanceShifts = totalPartialAttendanceShifts;
    }

    public List<IndexLeaveTypeDto> getShiftLeaveTypes() {
        return shiftLeaveTypes;
    }

    public void setShiftLeaveTypes(List<IndexLeaveTypeDto> shiftLeaveTypes) {
        this.shiftLeaveTypes = shiftLeaveTypes;
    }

    public Double getTotalPaidLeaveWorkRatio() {
        return totalPaidLeaveWorkRatio;
    }

    public void setTotalPaidLeaveWorkRatio(Double totalPaidLeaveWorkRatio) {
        this.totalPaidLeaveWorkRatio = totalPaidLeaveWorkRatio;
    }

    public Double getTotalUnpaidLeaveWorkRatio() {
        return totalUnpaidLeaveWorkRatio;
    }

    public void setTotalUnpaidLeaveWorkRatio(Double totalUnpaidLeaveWorkRatio) {
        this.totalUnpaidLeaveWorkRatio = totalUnpaidLeaveWorkRatio;
    }


}
