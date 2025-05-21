package com.globits.hr.service;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationItemDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface EvaluationItemService {
    ApiResponse<EvaluationItemDto> getById(UUID id);
    ApiResponse<List<EvaluationItemDto>> getAll();
    ApiResponse<EvaluationItemDto> save(EvaluationItemDto dto);
    void delete(UUID id);
    ApiResponse<Boolean> markDeleted(UUID id);
    ApiResponse<Page<EvaluationItemDto>> paging(SearchDto searchDto);

    Integer saveList(List<EvaluationItemDto> list);

    String autoGenerateCode(String configKey);
}
