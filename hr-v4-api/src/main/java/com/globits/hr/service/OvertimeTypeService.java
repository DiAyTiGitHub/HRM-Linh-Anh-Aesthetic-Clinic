/*
 * Created by TA & Giang on 22/4/2018.
 */

package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.OvertimeType;
import com.globits.hr.dto.OvertimeTypeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface OvertimeTypeService extends GenericService<OvertimeType, UUID> {
    OvertimeTypeDto saveOvertimeType(OvertimeTypeDto dto);

    Boolean deleteOvertimeType(UUID id);

    OvertimeTypeDto getOvertimeType(UUID id);

    Page<OvertimeTypeDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);
}
