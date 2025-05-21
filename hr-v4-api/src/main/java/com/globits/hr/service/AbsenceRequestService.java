package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.AbsenceRequestDto;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.timesheet.domain.AbsenceRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface AbsenceRequestService extends GenericService<AbsenceRequest, UUID> {
    Page<AbsenceRequestDto> pagingAbsenceRequestDto(AbsenceRequestSearchDto dto);

    AbsenceRequestDto saveOrUpdate(AbsenceRequestDto dto);

    AbsenceRequestDto getById(UUID id);

    Boolean deleteById(UUID id);

	List<UUID> updateRequestsApprovalStatus(AbsenceRequestSearchDto searchDto);

	Boolean deleteMultiple(List<UUID> listId);

}
