package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffAnnualLeaveHistory;
import com.globits.hr.dto.StaffAnnualLeaveHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffAnnualLeaveHistoryDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffAnnualLeaveHistoryService extends GenericService<StaffAnnualLeaveHistory, UUID> {
    Page<StaffAnnualLeaveHistoryDto> searchByPage(SearchStaffAnnualLeaveHistoryDto dto);

    StaffAnnualLeaveHistoryDto getById(UUID id);

    StaffAnnualLeaveHistoryDto saveOrUpdate(StaffAnnualLeaveHistoryDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    SearchStaffAnnualLeaveHistoryDto getInitialFilter();
}
