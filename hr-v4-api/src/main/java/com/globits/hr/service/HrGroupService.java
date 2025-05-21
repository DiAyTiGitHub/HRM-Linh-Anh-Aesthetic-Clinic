package com.globits.hr.service;


import com.globits.hr.dto.HrGroupDto;
import com.globits.hr.dto.search.SearchHrGroupDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrGroupService {
   public Page<HrGroupDto> pagingHrGroup(SearchHrGroupDto dto);

      HrGroupDto getById(UUID id);

     HrGroupDto saveHrGroup(HrGroupDto dto);

      Boolean deleteHrGroup(UUID id);
//
     Boolean deleteMultipleHrGroup(List<UUID> ids);
}
