package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.HrDocumentItem;
import com.globits.hr.dto.HrDocumentItemDto;
import com.globits.hr.dto.search.SearchHrDocumentItemDto;
import com.globits.salary.dto.SalaryItemDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrDocumentItemService extends GenericService<HrDocumentItem, UUID> {

    Page<HrDocumentItemDto> pagingHrDocumentItem(SearchHrDocumentItemDto dto);

    HrDocumentItemDto getById(UUID id);

    HrDocumentItemDto saveHrDocumentItem(HrDocumentItemDto dto);

    Integer saveListHrDocumentItems(List<HrDocumentItemDto> dtos);

    Boolean deleteHrDocumentItem(UUID id);

    Boolean deleteMultipleHrDocumentItems(List<UUID> ids);

    HrDocumentItemDto findByCode(String code);

    Boolean isValidCode(HrDocumentItemDto dto);
}