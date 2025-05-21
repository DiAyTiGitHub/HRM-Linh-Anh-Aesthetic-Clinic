package com.globits.hr.service;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationTemplateDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface EvaluationTemplateService {
    ApiResponse<Boolean> save(EvaluationTemplateDto dto);

    ApiResponse<EvaluationTemplateDto> getById(UUID id);
    ApiResponse<Page<EvaluationTemplateDto>> paging(SearchDto searchDto);
}
