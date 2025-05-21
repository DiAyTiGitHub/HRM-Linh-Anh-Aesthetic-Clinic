package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

// các vị tri tuyn thay thế
@Table(name = "tbl_position_recruitment_request")
@Entity
public class PositionRecruitmentRequest extends BaseObject {
    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private RecruitmentRequest request;

    private UUID previousStaffId;
    private String previousStaffDisplayName;


    private String note;

    public RecruitmentRequest getRequest() {
        return request;
    }

    public void setRequest(RecruitmentRequest request) {
        this.request = request;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public UUID getPreviousStaffId() {
        return previousStaffId;
    }

    public void setPreviousStaffId(UUID previousStaffId) {
        this.previousStaffId = previousStaffId;
    }

    public String getPreviousStaffDisplayName() {
        return previousStaffDisplayName;
    }

    public void setPreviousStaffDisplayName(String previousStaffDisplayName) {
        this.previousStaffDisplayName = previousStaffDisplayName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
