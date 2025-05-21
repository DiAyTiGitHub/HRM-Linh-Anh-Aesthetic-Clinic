package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.PositionRelationShip;

import java.util.UUID;

public class PositionRelationshipDto extends BaseObjectDto {
    private UUID positionId;
    private PositionDto position;
    private PositionDto supervisor;
    private Integer relationshipType; // Loại mối quan hệ của các vị trí. HRConstants.PositionRelationshipType
    private HRDepartmentDto department;

    public PositionRelationshipDto() {

    }

    public PositionRelationshipDto(PositionRelationShip entity) {
        this.id = entity.getId();
        this.relationshipType = entity.getRelationshipType();

        if (entity.getPosition() != null) {
            this.positionId = entity.getPosition().getId();
        }

        if (entity.getSupervisor() != null) {
            this.supervisor = new PositionDto(entity.getSupervisor());
        }

        if (entity.getDepartment() != null) {
            this.department = new HRDepartmentDto(entity.getDepartment());
        }
        if (entity.getPosition() != null) {
            this.position = new PositionDto(entity.getPosition(), false);
        }
    }

    public UUID getPositionId() {
        return positionId;
    }

    public void setPositionId(UUID positionId) {
        this.positionId = positionId;
    }

    public PositionDto getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(PositionDto supervisor) {
        this.supervisor = supervisor;
    }

    public Integer getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(Integer relationshipType) {
        this.relationshipType = relationshipType;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }
}
