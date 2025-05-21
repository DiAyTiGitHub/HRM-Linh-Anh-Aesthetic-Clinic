package com.globits.hr.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import com.globits.core.domain.BaseObject;

/*
 * Ca làm việc
 */
@Table(name = "tbl_shift_work")
@Entity
public class ShiftWork extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "total_hours", nullable = true)
    private Double totalHours;

    @Column(name = "shift_work_type")
    private Integer shiftWorkType; // Loại ca làm việc. Chi tiết: HrConstants.ShiftWorkType

    @OneToMany(mappedBy = "shiftWork", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startTime")
    private Set<ShiftWorkTimePeriod> timePeriods = new HashSet<>();

    @OneToMany(mappedBy = "shiftWork", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrDepartmentShiftWork> departmentShiftWorks; // các phòng ban áp dụng ca làm việc này

//    private Boolean isPaidByPeriod = false; // tính công tách theo khung giờ hay tính theo toàn ca

//    @Column(name = "work_radio")
//    private Double workRatio; // Tỉ lệ ngày công. VD: 1 ngày công
    
//    @Column(name="min_timekeeping_hour")
//    private Double minTimekeepingHour; // Thời gian tối thiểu để tính chấm công
    
	@Column(name = "converted_working_hours", nullable = true)
	private Double convertedWorkingHours; // Số giờ công quy đổi của ca làm việc

    public Set<HrDepartmentShiftWork> getDepartmentShiftWorks() {
        return departmentShiftWorks;
    }

    public void setDepartmentShiftWorks(Set<HrDepartmentShiftWork> departmentShiftWorks) {
        this.departmentShiftWorks = departmentShiftWorks;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<ShiftWorkTimePeriod> getTimePeriods() {
        return timePeriods;
    }

    public void setTimePeriods(Set<ShiftWorkTimePeriod> timePeriods) {
        this.timePeriods = timePeriods;
    }

    public Integer getShiftWorkType() {
        return shiftWorkType;
    }

    public void setShiftWorkType(Integer shiftWorkType) {
        this.shiftWorkType = shiftWorkType;
    }

	public Double getConvertedWorkingHours() {
		return convertedWorkingHours;
	}

	public void setConvertedWorkingHours(Double convertedWorkingHours) {
		this.convertedWorkingHours = convertedWorkingHours;
	}
    
    
	
}
