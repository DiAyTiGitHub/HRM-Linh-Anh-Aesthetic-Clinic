package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffAllowanceHistory;
import com.globits.hr.dto.StaffAllowanceHistoryDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffAllowanceHistoryService extends GenericService<StaffAllowanceHistory, UUID> {
    Page<StaffAllowanceHistoryDto> getPage(int pageIndex , int pageSize);
    List<StaffAllowanceHistoryDto> getAll(UUID id);
    StaffAllowanceHistoryDto getStaffAllowanceHistoryById(UUID id);
    StaffAllowanceHistoryDto saveStaffAllowanceHistory(StaffAllowanceHistoryDto dto ,UUID id);
    Boolean removeLists (List<UUID> ids);
    StaffAllowanceHistoryDto removeStaffAllowanceHistory(UUID id);
}
