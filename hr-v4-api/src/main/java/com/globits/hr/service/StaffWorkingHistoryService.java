package com.globits.hr.service;
import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffWorkingHistory;
import com.globits.hr.dto.LeaveHistoryDto;
import com.globits.hr.dto.StaffWorkingHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffWorkingHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.UUID;
@Repository
public interface StaffWorkingHistoryService extends GenericService<StaffWorkingHistory, UUID> {
    Page<StaffWorkingHistoryDto> getPage(SearchStaffWorkingHistoryDto searchDto );

    StaffWorkingHistoryDto saveStaffWorkingHistory(StaffWorkingHistoryDto dto);

    StaffWorkingHistoryDto getStaffWorkingHistory(UUID id);

    Boolean deleteStaffWorkingHistory(UUID id);

    StaffWorkingHistoryDto getRecentStaffWorkingHistory(UUID staffId);
    
    HashMap<UUID, LeaveHistoryDto> getLeaveHistoryMap();
    
}
