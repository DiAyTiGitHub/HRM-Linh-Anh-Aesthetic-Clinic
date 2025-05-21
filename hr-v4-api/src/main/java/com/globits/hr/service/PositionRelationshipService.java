package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.PositionRelationShip;
import com.globits.hr.dto.OrganizationChartRelationDto;
import com.globits.hr.dto.PositionDto;

import java.util.UUID;

public interface PositionRelationshipService extends GenericService<PositionRelationShip, UUID> {
    void handleSetRelationshipsInPotion(PositionDto dto, Position entity);

    void mappingWithChartRelation(OrganizationChartRelationDto dto);
}
