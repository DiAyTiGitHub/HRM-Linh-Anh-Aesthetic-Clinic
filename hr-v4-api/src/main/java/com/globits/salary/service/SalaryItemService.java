package com.globits.salary.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public interface SalaryItemService extends GenericService<SalaryItem, UUID> {
    SalaryItemDto saveSalaryItem(SalaryItemDto dto);

    Boolean deleteSalaryItem(UUID id);

    SalaryItemDto getSalaryItem(UUID id);

    Page<SalaryItemDto> searchByPage(SearchSalaryItemDto dto);

    Boolean deleteMultiple(List<UUID> ids);

    Boolean isValidCode(SalaryItemDto dto);

    boolean isSystemDefault(String code);

    SalaryItemDto findByCode(String code);

    List<SalaryItemDto> getByStaffId(UUID staffId);

    Integer saveListSalaryItem(List<SalaryItemDto> list);
}
