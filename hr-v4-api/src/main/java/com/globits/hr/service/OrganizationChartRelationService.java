/*
 * TA va Giang làm
 */

package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.OrganizationChartRelation;
import com.globits.hr.dto.OrganizationChartRelationDto;

import java.util.UUID;

public interface OrganizationChartRelationService extends GenericService<OrganizationChartRelation, UUID> {

    public OrganizationChartRelationDto saveOrUpdate(OrganizationChartRelationDto dto);

    public OrganizationChartRelationDto deleteById(UUID id);

    public OrganizationChartRelationDto savePositionRelationShip(OrganizationChartRelationDto dto);

    public Boolean deletePositionRelationShip(OrganizationChartRelationDto dto);
}
