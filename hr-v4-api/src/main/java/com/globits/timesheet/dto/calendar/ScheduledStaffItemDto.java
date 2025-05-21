package com.globits.timesheet.dto.calendar;

import com.globits.hr.domain.StaffWorkSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduledStaffItemDto {
    private Date workingDate;
    private List<ScheduledShiftWorkDto> shiftWorks; // các ca làm việc trong ngày
    private Double totalWorkingHours; // tổng số giờ làm việc thực tế trong ngày
    private Double totalAssignHours; // tổng số giờ làm việc đăng ký trong ngày

    // Các trường sau được cộng tổng với dữ liệu từ List<ScheduledShiftWorkDto> shiftWorks bên trên
    private Double staffTotalHours; // Số giờ nhân viên đã làm việc của ca này
    private Double convertedWorkingHours; // Số giờ công quy đổi của nhân viên
    private Integer lateArrivalCount; // Số lần đi làm muộn
    private Integer lateArrivalMinutes; // Số phút đi muộn
    private Integer earlyExitCount; // Số lần về sớm
    private Integer earlyExitMinutes; // Số phút về sớm
    private Integer earlyArrivalMinutes; // Số phút đến sớm
    private Integer lateExitMinutes; // Số phút về muộn
    private Double totalPaidWork; // công được tính của nhân viên. VD: 0.5 ngày công, 1 ngày công
    private Double confirmedOTHoursBeforeShift; // Số giờ làm thêm trước ca làm việc đã được xác nhận
    private Double confirmedOTHoursAfterShift; // Số giờ làm thêm sau ca làm việc đã được xác nhận

    public ScheduledStaffItemDto() {
        this.shiftWorks = new ArrayList<>();
    }

    public ScheduledStaffItemDto(Date workingDate, List<StaffWorkSchedule> scheduledStaffShiftWorks) {
        this.setWorkingDate(workingDate);

        this.shiftWorks = new ArrayList<>();

        double totalWorkingHours = 0.0;
        double totalAssignHours = 0.0;

        for (StaffWorkSchedule sws : scheduledStaffShiftWorks) {
            this.shiftWorks.add(new ScheduledShiftWorkDto(sws));

        }

        this.totalWorkingHours = totalWorkingHours;
        this.totalAssignHours = totalAssignHours;
    }

    public ScheduledStaffItemDto(Date workingDate, List<StaffWorkSchedule> scheduledStaffShiftWorks, Boolean getHourInfo) {
        this.setWorkingDate(workingDate);

        this.shiftWorks = new ArrayList<>();

        double totalWorkingHours = 0.0;
        double totalAssignHours = 0.0;

        for (StaffWorkSchedule sws : scheduledStaffShiftWorks) {
            this.shiftWorks.add(new ScheduledShiftWorkDto(sws));
            if (sws.getTotalValidHours() != null)
                totalWorkingHours += sws.getTotalValidHours();

            if (getHourInfo != null && getHourInfo.equals(true)) {
                if (sws.getShiftWork() != null) {
                    totalAssignHours += sws.getShiftWork().getTotalHours();
                }
//                if (sws.getTimesheetDetails() != null && !sws.getTimesheetDetails().isEmpty()) {
//                    if (!CollectionUtils.isEmpty(sws.getTimesheetDetails())) {
//                        double totalCheckedHours = sws.getTimesheetDetails().stream()
//                                .filter(detail -> detail.getStartTime() != null && detail.getEndTime() != null)
//                                .mapToDouble(detail -> {
//                                    Instant start = detail.getStartTime().toInstant();
//                                    Instant end = detail.getEndTime().toInstant();
//                                    return Duration.between(start, end).toMinutes() / 60.0;
//                                })
//                                .sum();
//
//                        totalWorkingHours += totalCheckedHours;
//                    }
//                }
            }

        }

        this.totalWorkingHours = totalWorkingHours;
        this.totalAssignHours = totalAssignHours;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public List<ScheduledShiftWorkDto> getShiftWorks() {
        return shiftWorks;
    }

    public void setShiftWorks(List<ScheduledShiftWorkDto> shiftWorks) {
        this.shiftWorks = shiftWorks;
    }

    public Double getTotalWorkingHours() {
        return totalWorkingHours;
    }

    public void setTotalWorkingHours(Double totalWorkingHours) {
        this.totalWorkingHours = totalWorkingHours;
    }

    public Double getTotalAssignHours() {
        return totalAssignHours;
    }

    public void setTotalAssignHours(Double totalAssignHours) {
        this.totalAssignHours = totalAssignHours;
    }
}

