package com.globits.hr.service;

import com.globits.core.domain.GlobalProperty;
import com.globits.core.service.GenericService;
import com.globits.hr.dto.HrGlobalPropertyDto;

import java.util.List;
import java.util.UUID;

public interface HrGlobalPropertyService extends GenericService<GlobalProperty, UUID> {

    HrGlobalPropertyDto saveGlobalProperty(HrGlobalPropertyDto dto, String id);

    void remove(String id);

    List<HrGlobalPropertyDto> getList();

    HrGlobalPropertyDto findGlobalProperty(String property);
}
