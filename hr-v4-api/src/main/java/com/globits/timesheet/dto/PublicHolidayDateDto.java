package com.globits.timesheet.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.timesheet.domain.PublicHolidayDate;
import jakarta.persistence.Column;

import java.util.Date;

public class PublicHolidayDateDto extends BaseObjectDto {

    private Date holidayDate;
    private Integer holidayType;
    private Double salaryCoefficient;
    private String description;
    private Boolean isHalfDayOff; // Chỉ Nghỉ nửa ngày
    private Double leaveHours; // Số giờ nghỉ

    public PublicHolidayDateDto() {

    }

    public PublicHolidayDateDto(PublicHolidayDate entity) {
        super(entity);

        if (entity == null) {
            return;
        }

        this.id = entity.getId();
        this.holidayDate = entity.getHolidayDate();
        this.holidayType = entity.getHolidayType();
        this.salaryCoefficient = entity.getSalaryCoefficient();
        this.description = entity.getDescription();
        this.isHalfDayOff = entity.getIsHalfDayOff();
        this.leaveHours = entity.getLeaveHours();
    }

    public Double getLeaveHours() {
        return leaveHours;
    }

    public void setLeaveHours(Double leaveHours) {
        this.leaveHours = leaveHours;
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

    public Boolean getIsHalfDayOff() {
        return isHalfDayOff;
    }

    public void setIsHalfDayOff(Boolean isHalfDayOff) {
        this.isHalfDayOff = isHalfDayOff;
    }
}
