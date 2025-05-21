package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffOverseasWorkHistory;
import com.globits.hr.dto.StaffOverseasWorkHistoryDto;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface StaffOverseasWorkHistoryService extends GenericService<StaffOverseasWorkHistory, UUID> {
    Page<StaffOverseasWorkHistoryDto> getPage(int pageIndex , int pageSize);
    List<StaffOverseasWorkHistoryDto> getAll(UUID id);
    StaffOverseasWorkHistoryDto getStaffOverseasWorkHistoryById(UUID id);
    StaffOverseasWorkHistoryDto saveStaffOverseasWorkHistory(StaffOverseasWorkHistoryDto dto ,UUID id);
    Boolean removeLists (List<UUID> ids);
    StaffOverseasWorkHistoryDto removeStaffOverseasWorkHistory(UUID id);
}
