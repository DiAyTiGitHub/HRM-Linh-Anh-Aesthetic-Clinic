package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.KPIItem;
import com.globits.hr.dto.KPIItemDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface KPIItemService extends GenericService<KPIItem, UUID> {
    Page<KPIItemDto> paging(SearchDto dto);
}
