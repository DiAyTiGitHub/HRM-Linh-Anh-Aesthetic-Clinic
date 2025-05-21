package com.globits.hr.service;

import com.globits.core.dto.SearchDto;
import com.globits.hr.dto.DeferredTypeDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DeferredTypeService {

    DeferredTypeDto saveOrUpdate(DeferredTypeDto dto);

    Page<DeferredTypeDto> searchByPage(SearchDto searchDto);

    DeferredTypeDto getById(UUID id);

    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    DeferredTypeDto findByCode(String code);

    Boolean checkCode(DeferredTypeDto dto);
}