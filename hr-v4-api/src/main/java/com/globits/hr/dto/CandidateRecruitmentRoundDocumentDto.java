package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.domain.CandidateRecruitmentRoundDocument;

public class CandidateRecruitmentRoundDocumentDto extends BaseObjectDto {

    private String note; // ghi chú về tài liệu/hồ sơ ứng viên đã nộp
    private FileDescriptionDto file; // tài liệu/hồ sơ ứng viên đã nộp
    private Integer displayOrder;

    CandidateRecruitmentRoundDocumentDto(CandidateRecruitmentRoundDocument entity) {
        super(entity);

        if(entity.getFile() != null) {
            this.file = new FileDescriptionDto(entity.getFile());
        }
        this.note = entity.getNote();
        this.displayOrder = entity.getDisplayOrder();
    }

    CandidateRecruitmentRoundDocumentDto() {
    }

    CandidateRecruitmentRoundDocumentDto(String note, FileDescriptionDto file) {
        this.note = note;
        this.file = file;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public FileDescriptionDto getFile() {
        return file;
    }

    public void setFile(FileDescriptionDto file) {
        this.file = file;
    }
}
