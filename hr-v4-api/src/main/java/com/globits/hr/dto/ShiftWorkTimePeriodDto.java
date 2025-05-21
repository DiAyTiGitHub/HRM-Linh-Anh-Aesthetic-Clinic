package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.core.dto.BaseObjectDto;
import jakarta.persistence.Column;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class ShiftWorkTimePeriodDto extends BaseObjectDto {
    private ShiftWorkDto shiftWorkDto;
    private Date endTime;
    private Date startTime;
    private String code;
    private String displayTime;
    private Double allowedLateMinutes; // Số phút đi muộn cho phép
    private Double workRatio; // Tỉ lệ ngày công. VD: 0.375 ngày công
    private Double minTimekeepingHour; // Thời gian tối thiểu để tính chấm công
    private Double minWorkTimeHour; // Thời gian tối thiểu để tính đã đi làm

    public ShiftWorkTimePeriodDto() {

    }

    public ShiftWorkTimePeriodDto(ShiftWorkTimePeriod timePeriod) {
        super(timePeriod);
        if (timePeriod == null) return;

        this.code = timePeriod.getCode();
        this.endTime = timePeriod.getEndTime();
        this.startTime = timePeriod.getStartTime();
        if (timePeriod.getShiftWork() != null) {
            this.shiftWorkDto = new ShiftWorkDto(timePeriod.getShiftWork());
        }
        this.displayTime = DateTimeUtil.getHourMinutes(timePeriod.getStartTime()) + " - " + DateTimeUtil.getHourMinutes(timePeriod.getEndTime());
        this.allowedLateMinutes = timePeriod.getAllowedLateMinutes(); // Số phút đi muộn cho phép
        this.workRatio = timePeriod.getWorkRatio(); // Tỉ lệ ngày công. VD: 0.375 ngày công
        this.minTimekeepingHour = timePeriod.getMinTimekeepingHour(); // Thời gian tối thiểu để tính chấm công
        this.minWorkTimeHour = timePeriod.getMinWorkTimeHour(); // Thời gian tối thiểu để tính đã đi làm
    }

    public ShiftWorkTimePeriodDto(ShiftWorkTimePeriod timePeriod, boolean collapse) {
        super(timePeriod);
        if (timePeriod == null) return;

        this.endTime = timePeriod.getEndTime();
        this.startTime = timePeriod.getStartTime();
        this.code = timePeriod.getCode();
        if (!collapse && timePeriod.getShiftWork() != null) {
            this.shiftWorkDto = new ShiftWorkDto();
            this.shiftWorkDto.setId(timePeriod.getShiftWork().getId());
            this.shiftWorkDto.setCode(timePeriod.getShiftWork().getCode());
            this.shiftWorkDto.setName(timePeriod.getShiftWork().getName());
        }

        this.displayTime = DateTimeUtil.getHourMinutes(timePeriod.getStartTime()) + " - " + DateTimeUtil.getHourMinutes(timePeriod.getEndTime());

        this.allowedLateMinutes = timePeriod.getAllowedLateMinutes(); // Số phút đi muộn cho phép
        this.workRatio = timePeriod.getWorkRatio(); // Tỉ lệ ngày công. VD: 0.375 ngày công
        this.minTimekeepingHour = timePeriod.getMinTimekeepingHour(); // Thời gian tối thiểu để tính chấm công
        this.minWorkTimeHour = timePeriod.getMinWorkTimeHour(); // Thời gian tối thiểu để tính đã đi làm
    }

    public ShiftWorkTimePeriod toEntity(ShiftWorkTimePeriodDto dto, ShiftWorkTimePeriod entity) {
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setCode(dto.getCode());
        entity.setAllowedLateMinutes(dto.getAllowedLateMinutes());
        entity.setMinTimekeepingHour(dto.getMinTimekeepingHour());
        entity.setMinWorkTimeHour(dto.getMinWorkTimeHour());
        entity.setWorkRatio(dto.getWorkRatio());

        return entity;
    }

    public ShiftWorkDto getShiftWorkDto() {
        return shiftWorkDto;
    }

    public void setShiftWorkDto(ShiftWorkDto shiftWorkDto) {
        this.shiftWorkDto = shiftWorkDto;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public Double getAllowedLateMinutes() {
        return allowedLateMinutes;
    }

    public void setAllowedLateMinutes(Double allowedLateMinutes) {
        this.allowedLateMinutes = allowedLateMinutes;
    }

    public Double getWorkRatio() {
        return workRatio;
    }

    public void setWorkRatio(Double workRatio) {
        this.workRatio = workRatio;
    }

    public Double getMinTimekeepingHour() {
        return minTimekeepingHour;
    }

    public void setMinTimekeepingHour(Double minTimekeepingHour) {
        this.minTimekeepingHour = minTimekeepingHour;
    }

    public Double getMinWorkTimeHour() {
        return minWorkTimeHour;
    }

    public void setMinWorkTimeHour(Double minWorkTimeHour) {
        this.minWorkTimeHour = minWorkTimeHour;
    }
}

