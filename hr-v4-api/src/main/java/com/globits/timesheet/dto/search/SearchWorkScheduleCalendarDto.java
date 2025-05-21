package com.globits.timesheet.dto.search;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.salary.dto.SalaryPeriodDto;

import java.util.*;

public class SearchWorkScheduleCalendarDto extends SearchStaffDto {
    //    private UUID staffId;
    private UUID organizationId;
    private HrOrganizationDto organization;
    private UUID departmentId;
    private HRDepartmentDto department;
    private UUID positionTitleId;
    private PositionTitleDto positionTitle;
    private UUID staffId;
    private StaffDto staff;

    private SalaryPeriodDto salaryPeriod;
    private UUID salaryPeriodId;
    private Date fromDate;
    private Date toDate;

    // used in get work calendar of staff
    private Integer chosenMonth;
    private Integer chosenYear;

    public SearchWorkScheduleCalendarDto() {
    }


    @Override
    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    @Override
    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    @Override
    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    @Override
    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }



    @Override
    public UUID getSalaryPeriodId() {
        return salaryPeriodId;
    }

    @Override
    public void setSalaryPeriodId(UUID salaryPeriodId) {
        this.salaryPeriodId = salaryPeriodId;
    }

    @Override
    public UUID getStaffId() {
        return staffId;
    }

    @Override
    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public UUID getDepartmentId() {
        return departmentId;
    }

    @Override
    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public Date getFromDate() {
        return fromDate;
    }

    @Override
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @Override
    public Date getToDate() {
        return toDate;
    }

    public Integer getChosenMonth() {
        return chosenMonth;
    }

    public void setChosenMonth(Integer chosenMonth) {
        this.chosenMonth = chosenMonth;
    }

    public Integer getChosenYear() {
        return chosenYear;
    }

    public void setChosenYear(Integer chosenYear) {
        this.chosenYear = chosenYear;
    }

    @Override
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public List<Date> getListDatesInRange() {
        List<Date> dates = new ArrayList<>();
        if (fromDate == null || toDate == null || fromDate.after(toDate)) {
            return dates;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        while (!calendar.getTime().after(toDate)) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1); // Move to the next day
        }

        return dates;
    }
}
