package com.globits.timesheet.service;

import com.globits.core.service.GenericService;
import com.globits.timesheet.domain.ShiftChangeRequest;
import com.globits.timesheet.dto.search.ShiftChangeRequestSearchDto;
import com.globits.timesheet.dto.ShiftChangeRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ShiftChangeRequestService extends GenericService<ShiftChangeRequest, UUID> {

    ShiftChangeRequestDto getById(UUID id);

    Boolean deleteById(UUID id);

    int markDelete(UUID id);

    ShiftChangeRequestDto saveOrUpdate(ShiftChangeRequestDto dto);

    List<UUID> updateApprovalStatus(ShiftChangeRequestSearchDto searchDto);

    Page<ShiftChangeRequestDto> searchByPage(ShiftChangeRequestSearchDto dto);

    ShiftChangeRequest getEntityById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    ShiftChangeRequestSearchDto getInitialFilter();
}
