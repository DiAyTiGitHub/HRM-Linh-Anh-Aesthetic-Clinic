package com.globits.hr.service;

import com.globits.core.dto.SearchDto;
import com.globits.hr.dto.LeavingJobReasonDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface LeavingJobReasonService {

    LeavingJobReasonDto saveOrUpdate(LeavingJobReasonDto dto);

    Page<LeavingJobReasonDto> searchByPage(SearchDto searchDto);

    LeavingJobReasonDto getById(UUID id);

    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    LeavingJobReasonDto findByCode(String code);

    Boolean checkCode(LeavingJobReasonDto dto);
}