package com.globits.hr.service;

import com.globits.core.dto.SearchDto;
import com.globits.hr.dto.AddendumTypeDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface AddendumTypeService {

    AddendumTypeDto saveOrUpdate(AddendumTypeDto dto);

    Page<AddendumTypeDto> searchByPage(SearchDto searchDto);

    AddendumTypeDto getById(UUID id);

    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    AddendumTypeDto findByCode(String code);

    Boolean checkCode(AddendumTypeDto dto);
}