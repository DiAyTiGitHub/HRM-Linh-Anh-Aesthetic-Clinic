package com.globits.hr.service;


import com.globits.core.dto.SearchDto;
import com.globits.hr.dto.DisciplineReasonDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DisciplineReasonService {
    public Page<DisciplineReasonDto> pagingDisciplineReasons(SearchDto dto);

    DisciplineReasonDto getById(UUID id);

    DisciplineReasonDto saveDisciplineReason(DisciplineReasonDto dto);

    Boolean deleteDisciplineReason(UUID id);

    Boolean deleteMultipleDisciplineReasons(List<UUID> ids);

    DisciplineReasonDto findByCode(String code);

    Boolean checkCode(DisciplineReasonDto dto);
}