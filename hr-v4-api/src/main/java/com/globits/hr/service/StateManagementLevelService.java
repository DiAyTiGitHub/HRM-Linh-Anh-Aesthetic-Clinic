package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StateManagementLevel;
import com.globits.hr.dto.StateManagementLevelDto;
import com.globits.hr.dto.search.SearchDto;

public interface StateManagementLevelService extends GenericService<StateManagementLevel, UUID>{
	StateManagementLevelDto saveStateManagementLevel(StateManagementLevelDto dto);

    Boolean deleteStateManagementLevel(UUID id);

    StateManagementLevelDto getStateManagementLevel(UUID id);

    StateManagementLevelDto updateStateManagementLevel(StateManagementLevelDto dto, UUID id);

    Page<StateManagementLevelDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);
}
