package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.SystemConfigDto;
import com.globits.hr.dto.search.SearchDto;

public interface SystemConfigService extends GenericService<SystemConfig, UUID> {

    Page<SystemConfigDto> pagingSystemConfig(SearchDto dto);

    SystemConfigDto saveOrUpdate(SystemConfigDto dto);

    SystemConfigDto getById(UUID id);

    Boolean deleteById(UUID id);

    Integer deleteMultiple(List<UUID> ids);

    Boolean checkKeyCode(SystemConfigDto dto);

    SystemConfigDto getByKeyCode(String configKey);

    SystemConfig getConfigByKey(String configKey, String configValue, String note);

    SystemConfig getConfigByConfigValue(String configValue);

    String generateNextCode(String prefix, int padding, String maxCode);
    String generateNextCodeKL(String prefix, String maxCode);
}
