package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffRewardHistory;
import com.globits.hr.dto.StaffRewardHistoryDto;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface StaffRewardHistoryService extends GenericService<StaffRewardHistory, UUID> {
    Page<StaffRewardHistoryDto> getPage(int pageIndex , int pageSize);
    List<StaffRewardHistoryDto> getAll(UUID id);
    StaffRewardHistoryDto getStaffRewardHistoryById(UUID id);
    StaffRewardHistoryDto saveStaffRewardHistory(StaffRewardHistoryDto dto ,UUID id);
    Boolean removeLists (List<UUID> ids);
    StaffRewardHistoryDto removeStaffRewardHistory(UUID id);
}
