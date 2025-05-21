package com.globits.hr.dto;

import java.util.Date;
import java.util.List;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.WorkingStatus;
import jakarta.persistence.Column;

public class StaffWorkScheduleListDto extends BaseObjectDto {
    private HRDepartmentDto department;
    private HrOrganizationDto organization;
    private PositionTitleDto positionTitle;
    private List<ShiftWorkDto> shiftWorks;
    private Integer workingType; // Loại làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingType
    private Double overtimeHours; // Thời gian đăng ký làm thêm. Có giá trị khi: workingType = EXTENDED_OVERTIME (Tăng ca kéo dài)

    private List<StaffDto> staffs;
    private Date fromDate;
    private Date toDate;
    private String name;

    private Integer timekeepingCalculationType;
    // on loop - Cho phân ca vào những hôm nào trong tuần
    private Boolean loopOnMonday;
    private Boolean loopOnTuesDay;
    private Boolean loopOnWednesday;
    private Boolean loopOnThursday;
    private Boolean loopOnFriday;
    private Boolean loopOnSaturday;
    private Boolean loopOnSunday;

    // Chỉ chấm công vào ra 1 lần
    private Boolean allowOneEntryOnly;
    // Cần xác nhận của người quản lý
    private Boolean needManagerApproval;


    public Boolean getNeedManagerApproval() {
        return needManagerApproval;
    }

    public void setNeedManagerApproval(Boolean needManagerApproval) {
        this.needManagerApproval = needManagerApproval;
    }

    public List<StaffDto> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<StaffDto> staffs) {
        this.staffs = staffs;
    }

    public List<ShiftWorkDto> getShiftWorks() {
        return shiftWorks;
    }

    public void setShiftWorks(List<ShiftWorkDto> shiftWorks) {
        this.shiftWorks = shiftWorks;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLoopOnMonday() {
        return loopOnMonday;
    }

    public void setLoopOnMonday(Boolean loopOnMonday) {
        this.loopOnMonday = loopOnMonday;
    }

    public Boolean getLoopOnTuesDay() {
        return loopOnTuesDay;
    }

    public void setLoopOnTuesDay(Boolean loopOnTuesDay) {
        this.loopOnTuesDay = loopOnTuesDay;
    }

    public Boolean getLoopOnWednesday() {
        return loopOnWednesday;
    }

    public void setLoopOnWednesday(Boolean loopOnWednesday) {
        this.loopOnWednesday = loopOnWednesday;
    }

    public Boolean getLoopOnThursday() {
        return loopOnThursday;
    }

    public void setLoopOnThursday(Boolean loopOnThursday) {
        this.loopOnThursday = loopOnThursday;
    }

    public Boolean getLoopOnFriday() {
        return loopOnFriday;
    }

    public void setLoopOnFriday(Boolean loopOnFriday) {
        this.loopOnFriday = loopOnFriday;
    }

    public Boolean getLoopOnSaturday() {
        return loopOnSaturday;
    }

    public void setLoopOnSaturday(Boolean loopOnSaturday) {
        this.loopOnSaturday = loopOnSaturday;
    }

    public Boolean getLoopOnSunday() {
        return loopOnSunday;
    }

    public void setLoopOnSunday(Boolean loopOnSunday) {
        this.loopOnSunday = loopOnSunday;
    }

    public Integer getWorkingType() {
        return workingType;
    }

    public void setWorkingType(Integer workingType) {
        this.workingType = workingType;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public Boolean getAllowOneEntryOnly() {
        return allowOneEntryOnly;
    }

    public void setAllowOneEntryOnly(Boolean allowOneEntryOnly) {
        this.allowOneEntryOnly = allowOneEntryOnly;
    }

    public Integer getTimekeepingCalculationType() {
        return timekeepingCalculationType;
    }

    public void setTimekeepingCalculationType(Integer timekeepingCalculationType) {
        this.timekeepingCalculationType = timekeepingCalculationType;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }
}
