package com.globits.hr.service;

import com.globits.hr.domain.EvaluationForm;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationFormDto;
import com.globits.hr.dto.StaffEvaluationDto;
import com.globits.hr.dto.search.EvaluationFormSearchDto;
import com.globits.hr.dto.view.EvaluationFormViewDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface EvaluationFormService {
    ApiResponse<EvaluationFormDto> getById(UUID id);
    ApiResponse<EvaluationFormDto> saveOrUpdate(EvaluationFormDto dto);
    ApiResponse<Boolean> deleteById(UUID id);
    ApiResponse<List<EvaluationFormDto>> getAll();
    ApiResponse<Boolean> markDeleteById(UUID id);
    ApiResponse<Page<EvaluationFormViewDto>> paging(EvaluationFormSearchDto searchDto);
    ApiResponse<Boolean> transferEvaluationForm(UUID id);
    ApiResponse<Boolean> staffEvaluate(List<StaffEvaluationDto> staffEvaluationDto);
    ApiResponse<Page<EvaluationForm>> pageForExcel(EvaluationFormSearchDto searchDto);
}
