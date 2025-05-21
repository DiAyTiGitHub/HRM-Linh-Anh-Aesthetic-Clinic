package com.globits.hr.dto;


import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.PositionRecruitmentRequest;
import com.globits.hr.domain.RecruitmentRequest;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

public class PositionRecruitmentRequestDto extends BaseObjectDto {


    private PositionDto position;
    private RecruitmentRequestDto request;

    private UUID previousStaffId;
    private String previousStaffDisplayName;
    private String note;

    public PositionRecruitmentRequestDto(PositionRecruitmentRequest entity) {
        if (entity.getPosition() != null) {
            this.position = new PositionDto(entity.getPosition(), false);
        }
//        if (entity.getRequest() != null) {
//            this.request = new RecruitmentRequestDto(entity.getRequest());
//        }
        this.previousStaffId = entity.getPreviousStaffId();
        this.previousStaffDisplayName = entity.getPreviousStaffDisplayName();
        this.note = entity.getNote();
    }

    public PositionRecruitmentRequestDto() {
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public RecruitmentRequestDto getRequest() {
        return request;
    }

    public void setRequest(RecruitmentRequestDto request) {
        this.request = request;
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
