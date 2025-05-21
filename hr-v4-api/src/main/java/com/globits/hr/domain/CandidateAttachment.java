package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.FileDescription;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_candidate_attachment")
public class CandidateAttachment extends BaseObject {
    private static final long serialVersionUID = 1L;
    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileDescription file;
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    // Loại tệp đính kèm, được định nghĩa trong HrConstants.CandidateAttachmentType
    private Integer attachmentType;
    private String name;
    private String note;

    public FileDescription getFile() {
        return file;
    }

    public void setFile(FileDescription file) {
        this.file = file;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
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
