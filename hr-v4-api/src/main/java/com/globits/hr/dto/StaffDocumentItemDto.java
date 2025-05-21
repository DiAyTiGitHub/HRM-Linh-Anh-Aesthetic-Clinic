package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.domain.StaffDocumentItem;

import java.util.Date;

public class StaffDocumentItemDto extends BaseObjectDto {
    private StaffDto staff; // Nhân viên nộp tài liệu
    private HrDocumentItemDto documentItem; // Tài liệu
    private Boolean isSubmitted; // Đã nộp hay chưa
    private Date submissionDate; // Ngày nộp
    private FileDescriptionDto file; // Tài liệu đã được lưu

    public StaffDocumentItemDto() {
    }

    public StaffDocumentItemDto(StaffDocumentItem entity) {
        super(entity);

        if (entity == null)
            return;

        if (entity.getStaff() != null) {
            this.staff = new StaffDto();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
            if (entity.getStaff().getDocumentTemplate() != null) {
                HrDocumentTemplateDto documentTemplate = new HrDocumentTemplateDto();
                documentTemplate.setId(entity.getStaff().getDocumentTemplate().getId());
                documentTemplate.setName(entity.getStaff().getDocumentTemplate().getName());
                documentTemplate.setCode(entity.getStaff().getDocumentTemplate().getCode());
                documentTemplate.setDescription(entity.getStaff().getDocumentTemplate().getDescription());
                this.staff.setDocumentTemplate(documentTemplate);
            }
        }

        this.documentItem = new HrDocumentItemDto(entity.getDocumentItem());
        this.isSubmitted = entity.getIsSubmitted();
        this.submissionDate = entity.getSubmissionDate();

        if (entity.getFile() != null)
            this.file = new FileDescriptionDto(entity.getFile());
    }

    public StaffDocumentItemDto(StaffDocumentItem entity, Boolean isGetFull) {
        super(entity);

        if (entity == null)
            return;

        this.documentItem = new HrDocumentItemDto(entity.getDocumentItem());
        this.isSubmitted = entity.getIsSubmitted();
        this.submissionDate = entity.getSubmissionDate();

        if (entity.getFile() != null)
            this.file = new FileDescriptionDto(entity.getFile());
        if (isGetFull) {
            if (entity.getStaff() != null) {
                this.staff = new StaffDto();
                this.staff.setId(entity.getStaff().getId());
                this.staff.setStaffCode(entity.getStaff().getStaffCode());
                this.staff.setDisplayName(entity.getStaff().getDisplayName());
                if (entity.getStaff().getDocumentTemplate() != null) {
                    HrDocumentTemplateDto documentTemplate = new HrDocumentTemplateDto();
                    documentTemplate.setId(entity.getStaff().getDocumentTemplate().getId());
                    documentTemplate.setName(entity.getStaff().getDocumentTemplate().getName());
                    documentTemplate.setCode(entity.getStaff().getDocumentTemplate().getCode());
                    documentTemplate.setDescription(entity.getStaff().getDocumentTemplate().getDescription());
                    this.staff.setDocumentTemplate(documentTemplate);
                }
            }
        }
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public HrDocumentItemDto getDocumentItem() {
        return documentItem;
    }

    public void setDocumentItem(HrDocumentItemDto documentItem) {
        this.documentItem = documentItem;
    }

    public Boolean getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(Boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public FileDescriptionDto getFile() {
        return file;
    }

    public void setFile(FileDescriptionDto file) {
        this.file = file;
    }

}
