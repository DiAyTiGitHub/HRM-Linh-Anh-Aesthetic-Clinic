package com.globits.hr.service;

import com.globits.core.dto.SearchDto;
import com.globits.hr.dto.RefusalReasonDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RefusalReasonService {

    RefusalReasonDto saveOrUpdate(RefusalReasonDto dto);

    Page<RefusalReasonDto> searchByPage(SearchDto searchDto);

    RefusalReasonDto getById(UUID id);

    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    RefusalReasonDto findByCode(String code);

    Boolean checkCode(RefusalReasonDto dto);
}