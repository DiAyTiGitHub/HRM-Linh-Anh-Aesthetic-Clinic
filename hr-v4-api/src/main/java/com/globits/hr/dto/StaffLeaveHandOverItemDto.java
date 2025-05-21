package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.StaffLeave;
import com.globits.hr.domain.StaffLeaveHandOverItem;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/*
 *  Các hạng mục bàn giao khi nghỉ việc
 */
public class StaffLeaveHandOverItemDto extends BaseObjectDto {
    private StaffLeaveDto staffLeave; // Nghỉ việc
    private UUID staffLeaveId;

    private Integer displayOrder; // thứ tự hiển thị
    private String name; // tên hạng mục cần bàn giao
    private String note; // Ghi chú
    private Date handoverDate; // Ngày bàn giao
    private Boolean isHandovered; // Đã nộp hay chưa


    public StaffLeaveHandOverItemDto() {

    }

    public StaffLeaveHandOverItemDto(StaffLeaveHandOverItem entity) {
        super(entity);

        if (entity.getStaffLeave() != null) {
            this.staffLeaveId = entity.getStaffLeave().getId();
        }

        this.displayOrder = entity.getDisplayOrder(); // Số quyết định nghỉ việc
        this.name = entity.getName(); // Ngày nghỉ việc
        this.note = entity.getNote();
        this.handoverDate = entity.getHandoverDate(); // Vẫn còn nợ. VD: Không / Chưa trả máy tính / Nợ tiền thuế
        this.isHandovered = entity.getIsHandovered(); // Trạng thái chi trả phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffPaidStatus
    }

    public StaffLeaveHandOverItemDto(StaffLeaveHandOverItem entity, boolean isDetail) {
        this(entity);

        if (!isDetail) return;


    }


    public UUID getStaffLeaveId() {
        return staffLeaveId;
    }

    public void setStaffLeaveId(UUID staffLeaveId) {
        this.staffLeaveId = staffLeaveId;
    }

    public StaffLeaveDto getStaffLeave() {
        return staffLeave;
    }

    public void setStaffLeave(StaffLeaveDto staffLeave) {
        this.staffLeave = staffLeave;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getHandoverDate() {
        return handoverDate;
    }

    public void setHandoverDate(Date handoverDate) {
        this.handoverDate = handoverDate;
    }

    public Boolean getIsHandovered() {
        return isHandovered;
    }

    public void setIsHandovered(Boolean isHandovered) {
        this.isHandovered = isHandovered;
    }
}
