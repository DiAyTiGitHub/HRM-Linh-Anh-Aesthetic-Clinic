package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryArea;
import com.globits.salary.domain.SalaryAutoMap;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.dto.SalaryAreaDto;
import com.globits.salary.dto.SalaryAutoMapDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SalaryAutoMapService extends GenericService<SalaryAutoMap, UUID> {
	SalaryAutoMapDto saveOrUpdate(SalaryAutoMapDto dto);

    SalaryAutoMapDto getById(UUID id);

    List<SalaryAutoMapDto> getAll(SearchDto searchDto);

    List<SalaryItem> getCorrespondingSalaryItems(HrConstants.SalaryAutoMapField mapField);

    List<String> getCorrespondingSalaryItemsCode(HrConstants.SalaryAutoMapField mapField);

    List<SalaryAutoMap> getBySalaryAutoMapField(HrConstants.SalaryAutoMapField mapField);

    boolean isAutoMapConstants(String code);
}
