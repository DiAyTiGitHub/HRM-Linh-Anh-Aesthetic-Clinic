package com.globits.hr.dto;

import java.util.Date;
import java.util.List;

import com.globits.hr.domain.WorkingStatus;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryTemplateDto;

public class SalaryCalculationRequestDto {
    private List<StaffDto> staffs;
    private SalaryPeriodDto salaryPeriod;
    private StaffDto staff;

    public List<StaffDto> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<StaffDto> staffs) {
        this.staffs = staffs;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }
}
