package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.domain.TimeSheetDetail;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/*
 * 
 */
@Table(name = "tbl_staff_work_schedule_shift_period")
@Entity
public class StaffWorkScheduleShiftPeriod extends BaseObject {
    private static final long serialVersionUID = 572369945947940265L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private StaffWorkSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_period_id")
    private ShiftWorkTimePeriod leavePeriod; // Giai đoạn nghỉ


    public StaffWorkSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(StaffWorkSchedule schedule) {
        this.schedule = schedule;
    }

    public ShiftWorkTimePeriod getLeavePeriod() {
        return leavePeriod;
    }

    public void setLeavePeriod(ShiftWorkTimePeriod leavePeriod) {
        this.leavePeriod = leavePeriod;
    }
}
