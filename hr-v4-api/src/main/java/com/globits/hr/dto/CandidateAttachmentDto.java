package com.globits.hr.dto;

import com.globits.core.domain.FileDescription;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.CandidateAttachment;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.UUID;

public class CandidateAttachmentDto extends BaseObjectDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private FileDescriptionDto file;
    private UUID candidateId;

    // Loại tệp đính kèm, được định nghĩa trong HrConstants.CandidateAttachmentType
    private Integer attachmentType;
    private String name;
    private String note;

    public CandidateAttachmentDto() {

    }

    public CandidateAttachmentDto(CandidateAttachment entity) {
        if (entity.getCandidate() != null) {
            this.candidateId = entity.getCandidate().getId();
        }
        if (entity.getFile() != null) {
            this.file = new FileDescriptionDto(entity.getFile());
        }
        this.attachmentType = entity.getAttachmentType();
        this.name = entity.getName();
        this.note = entity.getNote();
    }

    public FileDescriptionDto getFile() {
        return file;
    }

    public void setFile(FileDescriptionDto file) {
        this.file = file;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(Integer attachmentType) {
        this.attachmentType = attachmentType;
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
}
