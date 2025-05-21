package com.globits.task.service;


import java.util.List;
import java.util.UUID;

import com.globits.task.domain.HrSubTaskItem;
import com.globits.task.dto.HrSubTaskItemDto;
import com.globits.task.dto.SearchSubTaskDto;

public interface HrSubTaskItemService {
    Boolean delete(UUID id);

    HrSubTaskItemDto createOrUpdate(HrSubTaskItemDto dto, UUID id);

    HrSubTaskItemDto getById(UUID id);

    HrSubTaskItem getEntityById(UUID id);

    List<HrSubTaskItemDto> getListSubTaskItem(SearchSubTaskDto dto);
}
