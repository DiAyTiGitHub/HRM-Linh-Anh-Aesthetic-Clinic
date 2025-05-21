package com.globits.hr.dto;

import com.globits.core.dto.DepartmentDto;

import java.util.Set;

public class TransferStaffDto {
    PositionDto fromPosition;
    PositionDto toPosition;

    private String note;

    public TransferStaffDto() {
    }

    public PositionDto getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(PositionDto fromPosition) {
        this.fromPosition = fromPosition;
    }

    public PositionDto getToPosition() {
        return toPosition;
    }

    public void setToPosition(PositionDto toPosition) {
        this.toPosition = toPosition;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
