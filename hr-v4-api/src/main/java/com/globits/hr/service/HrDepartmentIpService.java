package com.globits.hr.service;

import com.globits.hr.dto.HrDepartmentIpDto;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrDepartmentIpService {

    HrDepartmentIpDto saveHrDepartmentIp(HrDepartmentIpDto dto);

    HrDepartmentIpDto getById(UUID id);

    Boolean deleteHrDepartmentIp(UUID id);

    Boolean deleteMultipleHrDepartmentIps(List<UUID> ids);

    public Page<HrDepartmentIpDto> pagingHrDepartmentIp(SearchHrDepartmentDto dto);

    // Kiểm tra địa chỉ IP có hợp lệ hay không
    Boolean isValidTimekeepingIP(UUID staffId, String timekeepingIP);
}
