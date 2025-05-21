package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.KPI;
import com.globits.hr.dto.KPIDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface KPIService  extends GenericService<KPI, UUID> {
    KPIDto getKPIById(UUID id);
    KPIDto saveOrUpdate(KPIDto dto);
    Boolean deleteById(UUID id);
    Page<KPIDto> paging(SearchDto dto);
    Boolean checkCode(KPIDto dto);
}
