package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.FileDescription;
import jakarta.persistence.*;

// Tài liệu/hồ sơ nhân viên đã nộp
@Entity
@Table(name = "tbl_candidate_recruitment_round_document")
public class CandidateRecruitmentRoundDocument extends BaseObject {
    private static final long serialVersionUID = -2208752009903206352L;

    @ManyToOne
    @JoinColumn(name = "candidate_recruitment_round_id")
    private CandidateRecruitmentRound round; // Tài liệu đã được lưu

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileDescription file; // Tài liệu đã được lưu

    private String note;

    private Integer displayOrder;

    public CandidateRecruitmentRoundDocument() {

    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public FileDescription getFile() {
        return file;
    }

    public void setFile(FileDescription file) {
        this.file = file;
    }

    public CandidateRecruitmentRound getRound() {
        return round;
    }

    public void setRound(CandidateRecruitmentRound round) {
        this.round = round;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
