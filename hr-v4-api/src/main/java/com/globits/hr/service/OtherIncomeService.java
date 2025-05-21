package com.globits.hr.service;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.OtherIncomeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchOtherIncomeDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OtherIncomeService {
    ApiResponse<OtherIncomeDto> getById(UUID id);

    ApiResponse<List<OtherIncomeDto>> getAll();

    ApiResponse<OtherIncomeDto> save(OtherIncomeDto dto);

    void delete(UUID id);

    ApiResponse<Boolean> markDeleted(UUID id);

    ApiResponse<Page<OtherIncomeDto>> paging(SearchOtherIncomeDto searchDto);

    ApiResponse<Boolean> deleteLists(List<UUID> ids);
}
