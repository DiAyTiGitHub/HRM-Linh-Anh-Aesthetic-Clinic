package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.HrDocumentTemplate;
import com.globits.hr.dto.DefaultDocumentTemplateItemDto;
import com.globits.hr.dto.HrDocumentTemplateDto;
import com.globits.hr.dto.search.SearchDto;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.UUID;

public interface HrDocumentTemplateService extends GenericService<HrDocumentTemplate, UUID> {
    HrDocumentTemplateDto getHrDocumentTemplateById(UUID id);

    HrDocumentTemplateDto getByCode(String code);

    HrDocumentTemplateDto saveOrUpdate(HrDocumentTemplateDto dto);

    Boolean deleteById(UUID id);

    Page<HrDocumentTemplateDto> paging(SearchDto dto);

    Boolean isValidCode(HrDocumentTemplateDto dto);

    HashMap<UUID, DefaultDocumentTemplateItemDto> getDefaultDocumentTemplateItemMap();


}
