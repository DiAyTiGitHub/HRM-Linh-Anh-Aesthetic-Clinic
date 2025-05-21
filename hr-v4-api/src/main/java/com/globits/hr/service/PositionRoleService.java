package com.globits.hr.service;

import com.globits.hr.dto.PositionRoleDto;
import com.globits.hr.dto.search.SearchPositionRole;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface PositionRoleService {
    PositionRoleDto savePositionRole(PositionRoleDto dto);

    PositionRoleDto getById(UUID id);

    Boolean deletePositionRole(UUID id);

    Boolean deleteMultiplePositionRoles(List<UUID> ids);

    Page<PositionRoleDto> pagingPositionRoles(SearchPositionRole dto);

    PositionRoleDto findByShortName(String shortName);

}
