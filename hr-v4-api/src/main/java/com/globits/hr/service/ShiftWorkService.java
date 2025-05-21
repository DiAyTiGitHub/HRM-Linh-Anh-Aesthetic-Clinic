package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ShiftWorkService extends GenericService<ShiftWork, UUID> {
    Page<ShiftWorkDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);

    void remove(UUID id);

    ShiftWorkDto getById(UUID id);

    List<ShiftWorkDto> getAll();

    ShiftWorkDto saveOrUpdate(UUID id, ShiftWorkDto dto);

    ShiftWorkDto findByCode(String code);

    Integer saveListShiftWork(List<ShiftWorkDto> list);
}
