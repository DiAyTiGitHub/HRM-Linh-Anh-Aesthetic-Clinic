package com.globits.hr.dto;

public class TotalStaffWorkScheduleDto {
    private String staffCode;
    private String staffName;

    private Double estimatedWorkingHours; //Số giờ làm việc ước tính (giờ)
    private Long lateArrivalCount; // Đi làm muộn (lần)
    private Long earlyExitCount; // Về sớm (lần)
    private Long lateArrivalMinutes; // Đi muộn (phút)
    private Long earlyExitMinutes; // Về sớm (phút)
    private Long earlyArrivalMinutes; // Đi sớm (phút)
    private Long lateExitMinutes; // Về muộn (phút)
    private Double confirmedOTHoursBeforeShift; //OT trước ca (giờ)
    private Double confirmedOTHoursAfterShift; //"OT sau ca (giờ)
    private Double totalHours; // Tổng số giờ làm (giờ)
    private Double totalValidHours; // Giờ làm hợp lệ (giờ)
    private Double paidLeaveHours; //Giờ nghỉ có lương (giờ)
    private Double unpaidLeaveHours; //"Giờ nghỉ không lương (giờ)
    private Double totalConfirmedOTHours; //tổng OT được xác nhận (giờ)
    private Double convertedWorkingHours; //Công quy đổi (giờ)
    private Double totalPaidWork; // Ngày công được tính (ngày công)
    private Double paidLeaveWorkRatio; //Nghỉ có lương (ngày công)
    private Double unpaidLeaveWorkRatio; //Nghỉ không lương (ngày công)

    public TotalStaffWorkScheduleDto() {
    }

    public TotalStaffWorkScheduleDto(String staffCode, String staffName, Double estimatedWorkingHours, Long lateArrivalCount, Long earlyExitCount, Long lateArrivalMinutes, Long earlyExitMinutes, Long earlyArrivalMinutes, Long lateExitMinutes, Double confirmedOTHoursBeforeShift, Double confirmedOTHoursAfterShift, Double totalHours, Double totalValidHours, Double paidLeaveHours, Double unpaidLeaveHours, Double convertedWorkingHours, Double totalPaidWork, Double unpaidLeaveWorkRatio, Double paidLeaveWorkRatio) {
        this.staffCode = staffCode;
        this.staffName = staffName;
        this.estimatedWorkingHours = estimatedWorkingHours;
        this.lateArrivalCount = lateArrivalCount;
        this.earlyExitCount = earlyExitCount;
        this.lateArrivalMinutes = lateArrivalMinutes;
        this.earlyExitMinutes = earlyExitMinutes;
        this.earlyArrivalMinutes = earlyArrivalMinutes;
        this.lateExitMinutes = lateExitMinutes;
        this.confirmedOTHoursBeforeShift = confirmedOTHoursBeforeShift;
        this.confirmedOTHoursAfterShift = confirmedOTHoursAfterShift;
        this.totalHours = totalHours;
        this.totalValidHours = totalValidHours;
        this.paidLeaveHours = paidLeaveHours;
        this.unpaidLeaveHours = unpaidLeaveHours;
        this.convertedWorkingHours = convertedWorkingHours;
        this.totalPaidWork = totalPaidWork;
        this.unpaidLeaveWorkRatio = unpaidLeaveWorkRatio;
        this.paidLeaveWorkRatio = paidLeaveWorkRatio;
        if (this.confirmedOTHoursAfterShift == null) {
            this.confirmedOTHoursAfterShift = 0.0;
        }
        if (this.confirmedOTHoursBeforeShift == null) {
            this.confirmedOTHoursBeforeShift = 0.0;
        }
        this.totalConfirmedOTHours = this.confirmedOTHoursAfterShift + this.confirmedOTHoursBeforeShift;
    }


    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Double getEstimatedWorkingHours() {
        return estimatedWorkingHours;
    }

    public void setEstimatedWorkingHours(Double estimatedWorkingHours) {
        this.estimatedWorkingHours = estimatedWorkingHours;
    }

    public Long getLateArrivalCount() {
        return lateArrivalCount;
    }

    public void setLateArrivalCount(Long lateArrivalCount) {
        this.lateArrivalCount = lateArrivalCount;
    }

    public Long getEarlyExitCount() {
        return earlyExitCount;
    }

    public void setEarlyExitCount(Long earlyExitCount) {
        this.earlyExitCount = earlyExitCount;
    }

    public Long getLateArrivalMinutes() {
        return lateArrivalMinutes;
    }

    public void setLateArrivalMinutes(Long lateArrivalMinutes) {
        this.lateArrivalMinutes = lateArrivalMinutes;
    }

    public Long getEarlyExitMinutes() {
        return earlyExitMinutes;
    }

    public void setEarlyExitMinutes(Long earlyExitMinutes) {
        this.earlyExitMinutes = earlyExitMinutes;
    }

    public Long getEarlyArrivalMinutes() {
        return earlyArrivalMinutes;
    }

    public void setEarlyArrivalMinutes(Long earlyArrivalMinutes) {
        this.earlyArrivalMinutes = earlyArrivalMinutes;
    }

    public Long getLateExitMinutes() {
        return lateExitMinutes;
    }

    public void setLateExitMinutes(Long lateExitMinutes) {
        this.lateExitMinutes = lateExitMinutes;
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

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public Double getTotalValidHours() {
        return totalValidHours;
    }

    public void setTotalValidHours(Double totalValidHours) {
        this.totalValidHours = totalValidHours;
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

    public Double getTotalConfirmedOTHours() {
        return totalConfirmedOTHours;
    }

    public void setTotalConfirmedOTHours(Double totalConfirmedOTHours) {
        this.totalConfirmedOTHours = totalConfirmedOTHours;
    }

    public Double getConvertedWorkingHours() {
        return convertedWorkingHours;
    }

    public void setConvertedWorkingHours(Double convertedWorkingHours) {
        this.convertedWorkingHours = convertedWorkingHours;
    }

    public Double getTotalPaidWork() {
        return totalPaidWork;
    }

    public void setTotalPaidWork(Double totalPaidWork) {
        this.totalPaidWork = totalPaidWork;
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
}
