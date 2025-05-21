package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffMaternityHistory;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.StaffMaternityHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffMaternityHistoryService extends GenericService<StaffMaternityHistory, UUID> {
    Page<StaffMaternityHistoryDto> getPage(int pageIndex, int pageSize);

    List<StaffMaternityHistoryDto> getAll(UUID id);

    StaffMaternityHistoryDto getStaffMaternityHistoryById(UUID id);

    StaffMaternityHistoryDto saveStaffMaternityHistory(StaffMaternityHistoryDto dto);

    Boolean removeLists(List<UUID> ids);

    StaffMaternityHistoryDto removeStaffMaternityHistory(UUID id);

    Page<StaffMaternityHistoryDto> searchByPage(SearchDto dto);

    void handleSetDuringPregnancyStatus(StaffWorkSchedule staffWorkSchedule);
}
