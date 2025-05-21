package com.globits.task.service;

import java.util.List;
import java.util.UUID;

import com.globits.task.domain.HrSubTask;
import com.globits.task.dto.HrSubTaskDto;
import com.globits.task.dto.SearchSubTaskDto;

public interface HrSubTaskService {
    Boolean delete(UUID id);

    HrSubTaskDto createOrUpdate(HrSubTaskDto dto, UUID id);

    HrSubTaskDto getById(UUID id);
    HrSubTask getEntityById(UUID id);

    List<HrSubTaskDto> getListSubTask(SearchSubTaskDto dto);
}
