package com.globits.hr.dto;

import com.globits.core.dto.DepartmentDto;
import com.globits.hr.domain.StaffWorkingHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globits.core.dto.BaseObjectDto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StaffWorkingHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(PositionStaffDto.class);
//    private DepartmentDto department;
//    private StaffDto staff;
//    private HRDepartmentDto hrDepartment;
//    private PositionTitleDto position;
//    private EmployeeStatusDto employeeStatus;
//    private Date startDate;
//    private Date endDate;
//    private String note;
//    private Boolean unpaidLeave;


    private Date startDate;
    private Date endDate;
    private StaffDto staff;
    private HrOrganizationDto fromOrganization;
    private HrOrganizationDto toOrganization;
    private HRDepartmentDto fromDepartment;
    private HRDepartmentDto toDepartment;
    private PositionDto fromPosition;
    private PositionDto toPosition;
    private Integer transferType;
    private String note;
    public StaffWorkingHistoryDto() {
    }

    public StaffWorkingHistoryDto(StaffWorkingHistory entity) {
        if (entity == null) return;

        setId(entity.getId());
        this.note = entity.getNote();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.transferType = entity.getTransferType();
        this.note = entity.getNote();

        if (entity.getStaff() != null) {
            this.staff = new StaffDto(entity.getStaff(), false, false);
        }

        if (entity.getFromOrganization() != null) {
            this.fromOrganization = new HrOrganizationDto(entity.getFromOrganization(), false, false);
        }

        if (entity.getToOrganization() != null) {
            this.toOrganization = new HrOrganizationDto(entity.getToOrganization(),false, false);
        }

        if (entity.getFromDepartment() != null) {
            this.fromDepartment = new HRDepartmentDto(entity.getFromDepartment(),false, false);
        }

        if (entity.getToDepartment() != null) {
            this.toDepartment = new HRDepartmentDto(entity.getToDepartment(),false, false);
        }

        if (entity.getFromPosition() != null) {
            this.fromPosition = new PositionDto(entity.getFromPosition(),false);
        }

        if (entity.getToPosition() != null) {
            this.toPosition = new PositionDto(entity.getToPosition(),false);
        }
    }


    public StaffWorkingHistoryDto(StaffWorkingHistory entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false)) return;


    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public HrOrganizationDto getFromOrganization() {
        return fromOrganization;
    }

    public void setFromOrganization(HrOrganizationDto fromOrganization) {
        this.fromOrganization = fromOrganization;
    }

    public HrOrganizationDto getToOrganization() {
        return toOrganization;
    }

    public void setToOrganization(HrOrganizationDto toOrganization) {
        this.toOrganization = toOrganization;
    }

    public HRDepartmentDto getFromDepartment() {
        return fromDepartment;
    }

    public void setFromDepartment(HRDepartmentDto fromDepartment) {
        this.fromDepartment = fromDepartment;
    }

    public HRDepartmentDto getToDepartment() {
        return toDepartment;
    }

    public void setToDepartment(HRDepartmentDto toDepartment) {
        this.toDepartment = toDepartment;
    }

    public PositionDto getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(PositionDto fromPosition) {
        this.fromPosition = fromPosition;
    }

    public PositionDto getToPosition() {
        return toPosition;
    }

    public void setToPosition(PositionDto toPosition) {
        this.toPosition = toPosition;
    }

    public Integer getTransferType() {
        return transferType;
    }

    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
