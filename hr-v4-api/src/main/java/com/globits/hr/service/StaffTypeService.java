package com.globits.hr.service;

import com.globits.core.dto.SearchDto;
import com.globits.hr.dto.StaffTypeDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffTypeService {

    StaffTypeDto saveOrUpdate(StaffTypeDto dto);

    Page<StaffTypeDto> searchByPage(SearchDto searchDto);

    StaffTypeDto getById(UUID id);

    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    StaffTypeDto findByCode(String code);

    Boolean checkCode(StaffTypeDto dto);
}