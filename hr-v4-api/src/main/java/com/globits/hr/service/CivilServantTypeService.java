package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.CivilServantType;
import com.globits.hr.dto.CivilServantTypeDto;
import com.globits.hr.dto.search.SearchDto;

public interface CivilServantTypeService extends GenericService<CivilServantType, UUID> {
    CivilServantTypeDto saveOrUpdate(CivilServantTypeDto dto, UUID id);

    CivilServantTypeDto getOne(UUID id);

    Page<CivilServantTypeDto> searchByPage(SearchDto dto);

    void deleteOne(UUID id);

    Boolean checkCode(UUID id, String code);

	Boolean checkName(UUID id, String name);

    CivilServantTypeDto findByCode(String code);
}
