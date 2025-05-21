package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.HrSpeciality;
import com.globits.hr.dto.HrSpecialityDto;
import com.globits.hr.dto.search.SearchDto;

public interface HrSpecialityService extends GenericService<HrSpeciality, UUID> {
    HrSpecialityDto getSpecialityDto(UUID id);

    Page<HrSpecialityDto> searchByPage(SearchDto dto);

    HrSpecialityDto saveSpeciality(HrSpecialityDto dto, UUID id);

    Boolean deleteSpeciality(UUID id);

    Boolean checkCode(UUID id, String code);

    HrSpecialityDto findByCode(String code);

    String autoGenerateCode(String configKey);
}

