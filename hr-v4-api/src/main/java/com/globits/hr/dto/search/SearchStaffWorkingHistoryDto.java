package com.globits.hr.dto.search;


import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.HrOrganization;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;

import java.util.Date;

public class SearchStaffWorkingHistoryDto extends SearchDto{
    private Date startDate;
    private Date endDate;
    private Staff staff;
    private HrOrganization fromOrganization;
    private HrOrganization toOrganization;
    private HRDepartment fromDepartment;
    private HRDepartment toDepartment;
    private Position fromPosition;
    private Position toPosition;
    private Integer transferType;

    public SearchStaffWorkingHistoryDto() {
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

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public HrOrganization getFromOrganization() {
        return fromOrganization;
    }

    public void setFromOrganization(HrOrganization fromOrganization) {
        this.fromOrganization = fromOrganization;
    }

    public HrOrganization getToOrganization() {
        return toOrganization;
    }

    public void setToOrganization(HrOrganization toOrganization) {
        this.toOrganization = toOrganization;
    }

    public HRDepartment getFromDepartment() {
        return fromDepartment;
    }

    public void setFromDepartment(HRDepartment fromDepartment) {
        this.fromDepartment = fromDepartment;
    }

    public HRDepartment getToDepartment() {
        return toDepartment;
    }

    public void setToDepartment(HRDepartment toDepartment) {
        this.toDepartment = toDepartment;
    }

    public Position getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(Position fromPosition) {
        this.fromPosition = fromPosition;
    }

    public Position getToPosition() {
        return toPosition;
    }

    public void setToPosition(Position toPosition) {
        this.toPosition = toPosition;
    }

    public Integer getTransferType() {
        return transferType;
    }

    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }
}
