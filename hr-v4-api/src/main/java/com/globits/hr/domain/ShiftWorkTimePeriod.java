package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;

@Table(name = "tbl_shift_work_time_period")
@Entity
public class ShiftWorkTimePeriod extends BaseObject {
    private static final long serialVersionUID = 6766395500871760165L;

    @ManyToOne
    @JoinColumn(name = "shift_work_id", nullable = false)
//    @JoinColumn(name = "shift_work_id")
    private ShiftWork shiftWork;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;
    
    @Column(name = "code")
    private String code;

    @Column(name = "allowed_late_minutes")
    private Double allowedLateMinutes; // Số phút đi muộn cho phép

    @Column(name = "work_radio")
    private Double workRatio; // Tỉ lệ ngày công. VD: 0.375 ngày công

    @Column(name="min_timekeeping_hour")
    private Double minTimekeepingHour; // Thời gian tối thiểu để tính chấm công

    @Column(name ="min_work_time_hour")
    private Double minWorkTimeHour; // Thời gian tối thiểu để tính đã đi làm

    public ShiftWork getShiftWork() {
        return shiftWork;
    }

    public void setShiftWork(ShiftWork shiftWork) {
        this.shiftWork = shiftWork;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
