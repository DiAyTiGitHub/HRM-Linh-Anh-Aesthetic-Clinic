package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.KPIResult;
import com.globits.hr.dto.KPIResultDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface KPIResultService extends GenericService<KPIResult, UUID> {
    List<KPIResultDto> getAll();

    KPIResultDto getKPIById(UUID id);

    KPIResultDto saveOrUpdate(KPIResultDto dto);

    Boolean deleteById(UUID id);

    Page<KPIResultDto> paging(SearchDto dto);
}
