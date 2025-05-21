package com.globits.hr.dto.search;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;

import java.util.UUID;

public class SearchHrIntroduceCostDto extends SearchDto {
    private StaffDto staff; // Nhân viên giới thiệu
    private UUID staffId; // Nhân viên giới thiệu
    private StaffDto introducedStaff; // Nhân viên được giới thiệu
    private UUID introducedStaffId; // Nhân viên được giới thiệu

    private UUID organizationId;
    private HrOrganizationDto organization;
    private UUID departmentId;
    private HRDepartmentDto department;
    private UUID positionTitleId;
    private PositionTitleDto positionTitle;

    private UUID introStaffOrganizationId;
    private HrOrganizationDto introStaffOrganization;
    private UUID introStaffDepartmentId;
    private HRDepartmentDto introStaffDepartment;
    private UUID introStaffPositionTitleId;
    private PositionTitleDto introStaffPositionTitle;

    private Integer month;
    private Integer year;

    public SearchHrIntroduceCostDto() {
    }


    @Override
    public UUID getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    @Override
    public UUID getDepartmentId() {
        return departmentId;
    }

    @Override
    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
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

    public UUID getIntroStaffOrganizationId() {
        return introStaffOrganizationId;
    }

    public void setIntroStaffOrganizationId(UUID introStaffOrganizationId) {
        this.introStaffOrganizationId = introStaffOrganizationId;
    }

    public HrOrganizationDto getIntroStaffOrganization() {
        return introStaffOrganization;
    }

    public void setIntroStaffOrganization(HrOrganizationDto introStaffOrganization) {
        this.introStaffOrganization = introStaffOrganization;
    }

    public UUID getIntroStaffDepartmentId() {
        return introStaffDepartmentId;
    }

    public void setIntroStaffDepartmentId(UUID introStaffDepartmentId) {
        this.introStaffDepartmentId = introStaffDepartmentId;
    }

    public HRDepartmentDto getIntroStaffDepartment() {
        return introStaffDepartment;
    }

    public void setIntroStaffDepartment(HRDepartmentDto introStaffDepartment) {
        this.introStaffDepartment = introStaffDepartment;
    }

    public UUID getIntroStaffPositionTitleId() {
        return introStaffPositionTitleId;
    }

    public void setIntroStaffPositionTitleId(UUID introStaffPositionTitleId) {
        this.introStaffPositionTitleId = introStaffPositionTitleId;
    }

    public PositionTitleDto getIntroStaffPositionTitle() {
        return introStaffPositionTitle;
    }

    public void setIntroStaffPositionTitle(PositionTitleDto introStaffPositionTitle) {
        this.introStaffPositionTitle = introStaffPositionTitle;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    @Override
    public UUID getStaffId() {
        return staffId;
    }

    @Override
    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public StaffDto getIntroducedStaff() {
        return introducedStaff;
    }

    public void setIntroducedStaff(StaffDto introducedStaff) {
        this.introducedStaff = introducedStaff;
    }

    public UUID getIntroducedStaffId() {
        return introducedStaffId;
    }

    public void setIntroducedStaffId(UUID introducedStaffId) {
        this.introducedStaffId = introducedStaffId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
