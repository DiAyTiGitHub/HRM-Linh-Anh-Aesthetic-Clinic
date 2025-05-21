package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryConfigItem;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.SalaryTemplateItemConfig;
import com.globits.salary.dto.SalaryConfigItemDto;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.SalaryTemplateItemDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SalaryTemplateItemConfigService extends GenericService<SalaryTemplateItemConfig, UUID> {

//    SalaryItemDto saveSalaryItem(SalaryItemDto dto);
//
//    Boolean deleteSalaryItem(UUID id);
//
//    SalaryItemDto getSalaryItem(UUID id);
//
//    Page<SalaryItemDto> searchByPage(SearchSalaryItemDto dto);
//
//    Boolean deleteMultiple(List<UUID> ids);
//
//    Boolean isValidCode(SalaryItemDto dto);
//
//    boolean isSystemDefault(String code);
//
//    SalaryItemDto findByCode(String code);

    void handleSetToSalaryTemplateItems(SalaryTemplateItem entity, SalaryTemplateItemDto dto);
}
