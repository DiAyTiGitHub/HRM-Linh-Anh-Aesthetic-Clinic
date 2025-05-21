package com.globits.hr.service;
/*
 * Modify Giang 21/04/2018
 */

import com.globits.core.service.GenericService;
import com.globits.hr.domain.WorkingStatus;
import com.globits.hr.dto.WorkingStatusDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchTaskDto;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface WorkingStatusService extends GenericService<WorkingStatus, UUID> {
    WorkingStatusDto saveOrUpdate(WorkingStatusDto dto);

    Boolean deleteWorkingStatus(UUID id);

    WorkingStatusDto getWorkingStatus(UUID id);

    WorkingStatus getEntityById(UUID id);

    WorkingStatus getEntityByCode(String code);

    Page<WorkingStatusDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);

    WorkingStatusDto findByCode(String code);

    List<WorkingStatusDto> getWorkingStatusWithTotalTasksByProject(SearchTaskDto searchObject);
}
