package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Department;

import jakarta.persistence.*;

import java.util.Date;

/*
 * Bảng lịch sử thuyên chuyển của nhân viên qua các phòng ban, đơn vị
 */
@Entity
@Table(name = "tbl_staff_working_history")
public class StaffWorkingHistory extends BaseObject {

    @Column(name = "start_date")
    private Date startDate; // ngày bắt đầu công tác

    @Column(name = "end_date")
    private Date endDate; // ngày kết thúc, nếu trường này là null thì là đến HIỆN TẠI vẫn đang công tác

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff; // nhân viên nào thuyên chuyển

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_organization_id")
    private HrOrganization fromOrganization; // từ đơn vị nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_organization_id")
    private HrOrganization toOrganization; // tới đơn vị nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_department_id")
    private HRDepartment fromDepartment; // từ phòng ban nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_department_id")
    private HRDepartment toDepartment; // tới phòng ban nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_position_id")
    private Position fromPosition; // từ vị trí nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_position_id")
    private Position toPosition; // tới vị trí nào

    @Column(name = "transfer_type")
    private Integer transferType; // Loại điều chuyển. Chi tiết tại: HrConstants.StaffWorkingHistoryTransferType

    @Column(name = "note", columnDefinition = "TEXT")
    private String note; // ghi chú

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getTransferType() {
        return transferType;
    }

    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }
}
