package com.globits.hr.service;


import com.globits.hr.dto.OrganizationBranchDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchOrganizationBranchDto;
import com.globits.hr.dto.search.SearchStaffDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OrganizationBranchService {
    public Page<OrganizationBranchDto> pagingOrganization(SearchOrganizationBranchDto dto);

    OrganizationBranchDto getById(UUID id);

    OrganizationBranchDto saveOrganizationBranch(OrganizationBranchDto dto);

    Boolean deleteOrganizationBranch(UUID id);

    Boolean deleteMultipleOrganizationBranches(List<UUID> ids);
}