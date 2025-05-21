package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.StaffAllowance;
import com.globits.hr.domain.StaffAnnualLeaveHistory;
import com.globits.hr.domain.StaffMonthlyLeaveHistory;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class StaffMonthlyLeaveHistoryDto extends BaseObjectDto {

    private UUID annualLeaveHistoryId; // Thuộc bảng thống kê nghỉ phép năm của nhân viên nào đó
    private Integer month; // Tháng thống kê nghỉ phép
    private Double leaveDays; // Số ngày nhân viên đã nghỉ trong tháng

    public StaffMonthlyLeaveHistoryDto() {

    }

    public StaffMonthlyLeaveHistoryDto(StaffMonthlyLeaveHistory entity) {
        if (entity == null) return;

        this.id = entity.getId();
        if (entity.getAnnualLeaveHistory() != null) {
            this.annualLeaveHistoryId = entity.getAnnualLeaveHistory().getId();
        }

        this.month = entity.getMonth();
        this.leaveDays = entity.getLeaveDays();
    }

    public UUID getAnnualLeaveHistoryId() {
        return annualLeaveHistoryId;
    }

    public void setAnnualLeaveHistoryId(UUID annualLeaveHistoryId) {
        this.annualLeaveHistoryId = annualLeaveHistoryId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Double leaveDays) {
        this.leaveDays = leaveDays;
    }
}
