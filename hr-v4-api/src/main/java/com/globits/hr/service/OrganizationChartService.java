/*
 * TA va Giang làm
 */

package com.globits.hr.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.globits.hr.domain.OrgChartData;
import com.globits.hr.domain.OrganizationChartRelation;
import com.globits.hr.dto.diagram.RequestGetChart;
import com.globits.hr.dto.diagram.ResponseSaveChart;
import org.springframework.data.domain.Page;
import com.globits.core.service.GenericService;
import com.globits.hr.domain.AcademicTitle;
import com.globits.hr.domain.OrganizationChart;
import com.globits.hr.dto.AcademicTitleDto;
import com.globits.hr.dto.OrgChartDataDto;
import com.globits.hr.dto.OrganizationChartDto;
import com.globits.hr.dto.OrganizationChartRelationDto;
import com.globits.hr.dto.search.OrgChartSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.security.dto.UserDto;

public interface OrganizationChartService extends GenericService<OrganizationChart, UUID> {

    public ResponseSaveChart saveOrUpdate(OrganizationChartDto dto);

    public OrgChartDataDto saveListOrganizationChart(List<OrganizationChartDto> dtos);

    public List<OrganizationChartDto> searchByDto(OrgChartSearchDto searchDto);

    public OrganizationChartDto deleteById(UUID id);

    public OrgChartDataDto saveOrgChartData(OrgChartDataDto dto);

    public OrgChartDataDto getOrgChartData(UUID id);

    public Page<OrgChartDataDto> pagingOrgChartData(OrgChartSearchDto searchDto);

    public Boolean deleteOrgChartData(UUID id);

    public Boolean deleteMultipleOrgChartData(List<UUID> ids);

    public UserDto getLineManager(String username);

    OrgChartDataDto syncPosition();

    OrgChartDataDto refreshOrgChartData(UUID id);

    Set<OrganizationChartRelation> generateEdges(OrgChartData orgChartData);

	OrgChartDataDto generateNodesFromNode(RequestGetChart requestGetChart);

    OrgChartDataDto generateNodesFromDepartment(RequestGetChart request);
}
