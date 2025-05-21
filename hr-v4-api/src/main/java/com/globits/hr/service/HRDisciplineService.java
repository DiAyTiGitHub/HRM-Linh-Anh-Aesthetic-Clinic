package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.HRDiscipline;
import com.globits.hr.dto.HRDisciplineDto;
import com.globits.hr.dto.search.SearchDto;

public interface HRDisciplineService extends GenericService<HRDiscipline, UUID> {
    Page<HRDisciplineDto> searchByPage(SearchDto dto);

    HRDisciplineDto saveOrUpdate(HRDisciplineDto dto, UUID id);

    HRDisciplineDto getOne(UUID id);

    void deleteOne(UUID id);

    Boolean checkCode(UUID id, String code);

    String autoGenerateCode(String configKey);
}
