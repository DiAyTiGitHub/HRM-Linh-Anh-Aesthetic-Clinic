package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.domain.SalaryResultStaffItem;

import java.util.*;

// Phiếu lương của nhân viên
public class SalaryResultStaffPaySlipDto extends BaseObjectDto {

    private SalaryResultDto salaryResult;
    private UUID salaryResultId;
    private StaffDto staff;
    private SalaryPeriodDto salaryPeriod;
    private SalaryTemplateDto salaryTemplate;

    // Giá trị của từng thành phần lương của nhân viên trong bảng lương => Gía trị của từng cell trong dòng dữ liệu
    private List<SalaryResultStaffPaySlipItemDto> salaryResultStaffItems;

    private String note;
    private Integer approvalStatus; // Trạng thái duyệt phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffApprovalStatus
    private Integer paidStatus; // Trạng thái chi trả phiếu lương
    private Boolean isLocked; // Phiếu lương đã bị khóa hay chưa (phụ thuộc vào bảng lương đã bị khóa hay chưa)


    public SalaryResultStaffPaySlipDto() {

    }

    public SalaryResultStaffPaySlipDto(SalaryResultStaff entity) {
        super();

        this.id = entity.getId();
        this.note = entity.getNote();
        this.approvalStatus = entity.getApprovalStatus();
        this.paidStatus = entity.getPaidStatus();

        if (entity.getSalaryResult() != null) {
            this.salaryResult = new SalaryResultDto();
            this.salaryResult.setId(entity.getSalaryResult().getId());
            this.salaryResult.setName(entity.getSalaryResult().getName());
            this.salaryResult.setCode(entity.getSalaryResult().getCode());

            this.salaryResultId = entity.getSalaryResult().getId();
            this.isLocked = entity.getSalaryResult().getIsLocked();
        }

        if (entity.getStaff() != null) {
            this.staff = new StaffDto();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
        }

        if (entity.getSalaryResult() != null && entity.getSalaryResult().getSalaryPeriod() != null) {
            this.salaryPeriod = new SalaryPeriodDto(entity.getSalaryResult().getSalaryPeriod());
        } else if (entity.getSalaryPeriod() != null) {
            this.salaryPeriod = new SalaryPeriodDto(entity.getSalaryPeriod());
        }

        if (entity.getSalaryTemplate() != null) {
            this.salaryTemplate = new SalaryTemplateDto(entity.getSalaryTemplate(), true);
        }

    }

    public SalaryResultStaffPaySlipDto(SalaryResultStaff entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

        if (entity.getSalaryResultStaffItems() != null && !entity.getSalaryResultStaffItems().isEmpty()) {
            List<SalaryResultStaffPaySlipItemDto> rowData = new ArrayList<>();

            for (SalaryResultStaffItem cell : entity.getSalaryResultStaffItems()) {
                SalaryResultStaffPaySlipItemDto cellDto = new SalaryResultStaffPaySlipItemDto(cell, true);

                rowData.add(cellDto);
            }

            Collections.sort(rowData, new Comparator<SalaryResultStaffPaySlipItemDto>() {
                @Override
                public int compare(SalaryResultStaffPaySlipItemDto o1, SalaryResultStaffPaySlipItemDto o2) {
                    // First, compare by displayOrder
                    if (o1.getReferenceDisplayOrder() == null && o2.getReferenceDisplayOrder() == null) return 0;
                    if (o1.getReferenceDisplayOrder() == null) return 1;
                    if (o2.getReferenceDisplayOrder() == null) return -1;

                    int orderComparison = o1.getReferenceDisplayOrder().compareTo(o2.getReferenceDisplayOrder());
                    if (orderComparison != 0) {
                        return orderComparison;
                    }

                    // If displayOrder is the same, compare by displayName (handling nulls)
                    if (o1.getReferenceName() == null && o2.getReferenceName() == null) return 0;
                    if (o1.getReferenceName() == null) return 1;
                    if (o2.getReferenceName() == null) return -1;
                    return o1.getReferenceName().compareTo(o2.getReferenceName());
                }
            });

            this.salaryResultStaffItems = rowData;
        }
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
    }

    public SalaryResultDto getSalaryResult() {
        return salaryResult;
    }

    public void setSalaryResult(SalaryResultDto salaryResult) {
        this.salaryResult = salaryResult;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public List<SalaryResultStaffPaySlipItemDto> getSalaryResultStaffItems() {
        return salaryResultStaffItems;
    }

    public void setSalaryResultStaffItems(List<SalaryResultStaffPaySlipItemDto> salaryResultStaffItems) {
        this.salaryResultStaffItems = salaryResultStaffItems;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public SalaryTemplateDto getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplateDto salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public UUID getSalaryResultId() {
        return salaryResultId;
    }

    public void setSalaryResultId(UUID salaryResultId) {
        this.salaryResultId = salaryResultId;
    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }


}
