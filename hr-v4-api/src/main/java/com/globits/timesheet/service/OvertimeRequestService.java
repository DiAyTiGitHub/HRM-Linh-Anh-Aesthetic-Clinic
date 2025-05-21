package com.globits.timesheet.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.timesheet.domain.OvertimeRequest;
import com.globits.timesheet.dto.OvertimeRequestDto;

import com.globits.timesheet.dto.search.SearchOvertimeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OvertimeRequestService extends GenericService<OvertimeRequest, UUID> {

    Page<OvertimeRequestDto> pagingOvertimeRequest(SearchOvertimeRequestDto dto);

    OvertimeRequestDto saveOrUpdate(OvertimeRequestDto dto);

    OvertimeRequestDto getById(UUID id);

    Boolean deleteById(UUID id);

    Integer updateRequestsApprovalStatus(List<OvertimeRequestDto> dtos);

    Boolean deleteMultiple(List<UUID> listId);

    SearchOvertimeRequestDto getInitialFilter();

    ByteArrayOutputStream importExcelOvertimeRequest(MultipartFile file) throws IOException;

}