package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.hr.dto.EmployeeStatusDto;
import com.globits.hr.dto.search.SearchDto;

public interface EmployeeStatusService {
    Page<EmployeeStatusDto> searchByPage(SearchDto dto);

    EmployeeStatusDto saveOrUpdate(EmployeeStatusDto dto);

    EmployeeStatusDto getOne(UUID id);

    void deleteOne(UUID id);

    Boolean checkCode(UUID id, String code);
}
