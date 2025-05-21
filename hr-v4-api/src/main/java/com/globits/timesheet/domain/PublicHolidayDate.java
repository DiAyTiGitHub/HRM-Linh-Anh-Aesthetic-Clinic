package com.globits.timesheet.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Date;

// Ngày nghỉ làm việc
@Table(name = "tbl_public_holiday_date")
@Entity
public class PublicHolidayDate extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "holiday_date")
    private Date holidayDate; // ngày nghỉ

    //1. Ngày làm việc bình thường: 100% lương cơ bản (hệ số 1.0).
    //2. Làm thêm giờ trong ngày thường:
    //   150% lương (hệ số 1.5) cho giờ làm thêm vào ngày thường.
    //   200% lương (hệ số 2.0) nếu làm vào ngày nghỉ hàng tuần.
    //3. Ngày nghỉ lễ, Tết (theo quy định của nhà nước):
    //   300% lương (hệ số 3.0) nếu làm việc trong ngày lễ nhưng chưa bao gồm lương ngày công.
    //   => Tổng cộng có thể là 400% (hệ số 4.0) nếu tính cả lương ngày công.
    //4. Làm thêm vào ban đêm (từ 22h đến 6h sáng hôm sau):
    //   Thêm 30% lương so với mức tính ban ngày.
    @Column(name = "salary_coefficient")
    private Double salaryCoefficient; // hệ số tính lương

    @Column(name = "holiday_type")
    private Integer holidayType; // Loại ngày nghỉ. Chi tiết: HrConstants.HolidayLeaveType

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_half_day_off")
    private Boolean isHalfDayOff; // Chỉ Nghỉ nửa ngày

    @Column(name = "leave_hours")
    private Double leaveHours; // Số giờ nghỉ


    public Double getLeaveHours() {
        return leaveHours;
    }

    public void setLeaveHours(Double leaveHours) {
        this.leaveHours = leaveHours;
    }

    public Boolean getIsHalfDayOff() {
		return isHalfDayOff;
	}

	public void setIsHalfDayOff(Boolean isHalfDayOff) {
		this.isHalfDayOff = isHalfDayOff;
	}

	public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    public Double getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(Double salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }

    public Integer getHolidayType() {
        return holidayType;
    }

    public void setHolidayType(Integer holidayType) {
        this.holidayType = holidayType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
