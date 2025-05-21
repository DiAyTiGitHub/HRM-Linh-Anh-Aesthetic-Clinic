package com.globits.hr.dto;

import java.util.Date;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.CivilServantCategoryGrade;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffCivilServantCategoryGrade;

public class StaffCivilServantGradeDto extends BaseObjectDto {
    private Boolean isCurrent;
    private Date fromDate;
    private Date toDate;
    private Staff staff;

    private CivilServantCategoryGrade civilServantCategoryGrade;

    public StaffCivilServantGradeDto() {
    }

    public StaffCivilServantGradeDto(StaffCivilServantCategoryGrade staffCivilServantCategoryGrade) {
        if (staffCivilServantCategoryGrade != null) {
            this.setId(staffCivilServantCategoryGrade.getId());
            this.setIsCurrent(staffCivilServantCategoryGrade.getIsCurrent());
            this.setFromDate(staffCivilServantCategoryGrade.getFromDate());
            this.setToDate(staffCivilServantCategoryGrade.getToDate());
            this.setStaff(staffCivilServantCategoryGrade.getStaff());
            this.setCivilServantCategoryGrade(staffCivilServantCategoryGrade.getCivilServantCategoryGrade());
        }
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public CivilServantCategoryGrade getCivilServantCategoryGrade() {
        return civilServantCategoryGrade;
    }

    public void setCivilServantCategoryGrade(CivilServantCategoryGrade civilServantCategoryGrade) {
        this.civilServantCategoryGrade = civilServantCategoryGrade;
    }

}