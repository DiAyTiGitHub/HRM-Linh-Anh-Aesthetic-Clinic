package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.Bank;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.StaffWorkSchedule;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

public class StaffWorkScheduleShiftPeriodDto extends BaseObjectDto {
    private UUID scheduleId;
    private StaffWorkSchedule schedule;
    private UUID leavePeriodId;
    private ShiftWorkTimePeriod leavePeriod; // Giai đoạn nghỉ

    public StaffWorkScheduleShiftPeriodDto() {
    }

    public UUID getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(UUID scheduleId) {
        this.scheduleId = scheduleId;
    }

    public StaffWorkSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(StaffWorkSchedule schedule) {
        this.schedule = schedule;
    }

    public UUID getLeavePeriodId() {
        return leavePeriodId;
    }

    public void setLeavePeriodId(UUID leavePeriodId) {
        this.leavePeriodId = leavePeriodId;
    }

    public ShiftWorkTimePeriod getLeavePeriod() {
        return leavePeriod;
    }

    public void setLeavePeriod(ShiftWorkTimePeriod leavePeriod) {
        this.leavePeriod = leavePeriod;
    }
}
