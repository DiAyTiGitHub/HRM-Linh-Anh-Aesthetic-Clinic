package com.globits.task.service;

import java.util.UUID;

import com.globits.task.dto.HrTaskDto;
import com.globits.task.dto.HrTaskStaffDto;

public interface HrTaskStaffService {
    Boolean delete(UUID id);

    HrTaskStaffDto createOrUpdate(HrTaskStaffDto dto, UUID id);
    HrTaskStaffDto createOrUpdateByUUID(UUID taskId, UUID staffId, UUID id);


}
