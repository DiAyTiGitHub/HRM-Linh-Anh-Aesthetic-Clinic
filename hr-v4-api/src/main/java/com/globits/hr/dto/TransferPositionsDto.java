package com.globits.hr.dto;

import com.globits.core.dto.DepartmentDto;

import java.util.Set;

public class TransferPositionsDto {
    DepartmentDto department;
    Set<PositionDto> positions;

    public TransferPositionsDto(DepartmentDto department, Set<PositionDto> positions) {
        this.department = department;
        this.positions = positions;
    }

    public TransferPositionsDto() {
    }

    public DepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDto department) {
        this.department = department;
    }

    public Set<PositionDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDto> positions) {
        this.positions = positions;
    }
}
