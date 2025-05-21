package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffAllowance;
import com.globits.hr.domain.StaffLeave;
import com.globits.hr.domain.StaffLeaveHandOverItem;
import jakarta.persistence.*;
import org.springframework.util.CollectionUtils;

import java.util.*;

/*
 *  Nghỉ việc
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class StaffLeaveDto extends BaseObjectDto {
    private StaffDto staff;
    private UUID staffId;

    private String decisionNumber; // Số quyết định nghỉ việc
    private Date leaveDate; // Ngày nghỉ việc
    private String stillInDebt; // Vẫn còn nợ. VD: Không / Chưa trả máy tính / Nợ tiền thuế
    private List<StaffLeaveHandOverItemDto> handleOverItems; // Các hạng mục bàn giao
    private Integer paidStatus; // Trạng thái chi trả phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffPaidStatus

    public StaffLeaveDto() {
    }

    public StaffLeaveDto(StaffLeave entity) {
        super(entity);

        if (entity.getStaff() != null) {
            this.staff = new StaffDto();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
            this.staffId = entity.getStaff().getId();
        }

        this.decisionNumber = entity.getDecisionNumber(); // Số quyết định nghỉ việc
        this.leaveDate = entity.getLeaveDate(); // Ngày nghỉ việc
        this.stillInDebt = entity.getStillInDebt(); // Vẫn còn nợ. VD: Không / Chưa trả máy tính / Nợ tiền thuế
        this.paidStatus = entity.getPaidStatus(); // Trạng thái chi trả phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffPaidStatus
    }

    public StaffLeaveDto(StaffLeave entity, boolean isDetail) {
        this(entity);

        if (!isDetail) return;

        this.handleOverItems = new ArrayList<>();

        for (StaffLeaveHandOverItem handOverItem : entity.getHandleOverItems()) {
            StaffLeaveHandOverItemDto responseItem = new StaffLeaveHandOverItemDto(handOverItem, true);

            this.handleOverItems.add(responseItem);
        }
        if (!CollectionUtils.isEmpty(this.handleOverItems)) {
            Collections.sort(this.handleOverItems, new Comparator<StaffLeaveHandOverItemDto>() {
                @Override
                public int compare(StaffLeaveHandOverItemDto o1, StaffLeaveHandOverItemDto o2) {
                    if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null) {
                        return 0;
                    }
                    if (o1.getDisplayOrder() == null) {
                        return -1;
                    }
                    if (o2.getDisplayOrder() == null) {
                        return 1;
                    }
                    return o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                }
            });
        }

    }


    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getDecisionNumber() {
        return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
        this.decisionNumber = decisionNumber;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getStillInDebt() {
        return stillInDebt;
    }

    public void setStillInDebt(String stillInDebt) {
        this.stillInDebt = stillInDebt;
    }

    public List<StaffLeaveHandOverItemDto> getHandleOverItems() {
        return handleOverItems;
    }

    public void setHandleOverItems(List<StaffLeaveHandOverItemDto> handleOverItems) {
        this.handleOverItems = handleOverItems;
    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }
}
