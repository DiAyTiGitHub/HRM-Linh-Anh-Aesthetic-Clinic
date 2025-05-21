package com.globits.hr.service;


import com.globits.core.domain.Organization;
import com.globits.core.dto.OrganizationDto;
import com.globits.hr.domain.OrganizationImage;
import com.globits.hr.dto.OrganizationBranchDto;
import com.globits.hr.dto.OrganizationExtDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OrganizationExtService {
    public List<Organization> findEntityByCode(String code);

    public Page<OrganizationDto> pagingOrganization(SearchDto dto);

    OrganizationExtDto getById(UUID orgId);

    OrganizationExtDto saveOrganization(OrganizationExtDto dto);

    Boolean deleteOrganization(UUID orgId);

    Boolean deleteMultipleOrganization(List<UUID> orgIds);

    OrganizationExtDto getCurrentOrganizationOfCurrentUser();
}