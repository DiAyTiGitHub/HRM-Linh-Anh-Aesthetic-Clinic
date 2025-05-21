package com.globits.hr.service;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.CreateInterviewSchedulesDto;
import com.globits.hr.dto.EvaluationItemDto;
import com.globits.hr.dto.InterviewScheduleDto;
import com.globits.hr.dto.search.InterviewScheduleSearchDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface InterviewScheduleService {
    ApiResponse<InterviewScheduleDto> getById(UUID id);

    ApiResponse<List<InterviewScheduleDto>> getAll();

    ApiResponse<InterviewScheduleDto> save(InterviewScheduleDto dto);

    public ApiResponse<Integer> saveMultiple(CreateInterviewSchedulesDto dto);

    void delete(UUID id);

    ApiResponse<Boolean> markDeleted(UUID id);
    
    ApiResponse<Page<InterviewScheduleDto>> paging(InterviewScheduleSearchDto dto);
}
