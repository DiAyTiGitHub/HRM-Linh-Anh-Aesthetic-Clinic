package com.globits.timesheet.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.StaffWorkSchedule;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

// Yêu cầu nghỉ phép
@Table(name = "tbl_absence_request")
@Entity
public class AbsenceRequest extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "work_schedule_id")
    private StaffWorkSchedule workSchedule; // yêu cầu nghỉ vào lịch nào

    @Column(name = "request_date")
    private Date requestDate; // ngày tạo yêu cầu nghỉ

    @Column(name = "absence_reason", columnDefinition = "TEXT")
    private String absenceReason; // Lý do yêu cầu

    @Column(name = "absence_type")
    private Integer absenceType; // Loại nghỉ phép. Chi tiết: HrConstants.AbsenceRequestType

    @Column(name = "approval_status")
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.AbsenceRequestApprovalStatus


    public StaffWorkSchedule getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(StaffWorkSchedule workSchedule) {
        this.workSchedule = workSchedule;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getAbsenceReason() {
        return absenceReason;
    }

    public void setAbsenceReason(String absenceReason) {
        this.absenceReason = absenceReason;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(Integer absenceType) {
        this.absenceType = absenceType;
    }
}
