package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffDisciplineHistory;
import com.globits.hr.dto.StaffDisciplineHistoryDto;
import com.globits.hr.dto.search.StaffDisciplineHistorySearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffDisciplineHistoryService extends GenericService<StaffDisciplineHistory, UUID> {
    Page<StaffDisciplineHistoryDto> searchByPage(StaffDisciplineHistorySearchDto dto);

    StaffDisciplineHistoryDto getById(UUID id);

    StaffDisciplineHistoryDto saveOrUpdate(StaffDisciplineHistoryDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);
}
