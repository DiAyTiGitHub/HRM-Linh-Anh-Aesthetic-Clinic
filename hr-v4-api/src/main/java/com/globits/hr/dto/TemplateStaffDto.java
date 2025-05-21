package com.globits.hr.dto;

import java.util.List;
import java.util.UUID;

public class TemplateStaffDto {
    private StaffDto staff;
    private UUID staffId;
    private HrDocumentTemplateDto documentTemplate;
    private UUID templateId;
    private List<StaffDocumentItemDto> staffDocumentItems; // các tài liệu nhân viên đã nộp
    private Integer staffDocumentStatus;

    public TemplateStaffDto() {
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public HrDocumentTemplateDto getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(HrDocumentTemplateDto documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public List<StaffDocumentItemDto> getStaffDocumentItems() {
        return staffDocumentItems;
    }

    public void setStaffDocumentItems(List<StaffDocumentItemDto> staffDocumentItems) {
        this.staffDocumentItems = staffDocumentItems;
    }

    public Integer getStaffDocumentStatus() {
        return staffDocumentStatus;
    }

    public void setStaffDocumentStatus(Integer staffDocumentStatus) {
        this.staffDocumentStatus = staffDocumentStatus;
    }

    public UUID getStaffId() {
        if (staff != null) {
            return staff.getId();
        } else {
            return null;
        }
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getTemplateId() {
        if (documentTemplate != null) {
            return documentTemplate.getId();
        } else {
            return null;
        }
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }
}
