package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffTrainingHistory;
import com.globits.hr.dto.StaffTrainingHistoryDto;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface StaffTrainingHistoryService extends GenericService<StaffTrainingHistory , UUID> {
    Page<StaffTrainingHistoryDto> getPage(int pageIndex , int pageSize);
    List<StaffTrainingHistoryDto> getAll(UUID id);
    StaffTrainingHistoryDto getStaffTrainingHistoryById(UUID id);
    StaffTrainingHistoryDto saveStaffTrainingHistory(StaffTrainingHistoryDto dto ,UUID id);
    Boolean removeLists (List<UUID> ids);
    StaffTrainingHistoryDto removeStaffTrainingHistory(UUID id);
}
