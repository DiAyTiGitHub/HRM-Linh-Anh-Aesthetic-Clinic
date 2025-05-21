package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffEducationHistory;
import com.globits.hr.dto.StaffEducationHistoryDto;

public interface StaffEducationHistoryService extends GenericService<StaffEducationHistory, UUID> {
    Page<StaffEducationHistoryDto> getPages(int pageIndex, int pageSize);

    List<StaffEducationHistoryDto> getAll(UUID id);

    StaffEducationHistoryDto getEducationById(UUID id);

    StaffEducationHistoryDto saveEducation(StaffEducationHistoryDto educationDto, UUID id);

    Boolean removeLists(List<UUID> ids);

    StaffEducationHistoryDto removeEducation(UUID id);

    StaffEducationHistoryDto saveImportStaffEducationHistory(StaffEducationHistoryDto dto);

    Page<StaffEducationHistoryDto> pagingEducationHistory(SearchDto dto);
}
