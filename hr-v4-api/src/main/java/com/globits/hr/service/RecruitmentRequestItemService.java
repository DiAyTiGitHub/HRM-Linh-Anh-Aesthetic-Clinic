package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.RecruitmentRequestItem;
import com.globits.hr.dto.RecruitmentRequestItemDto;
import com.globits.hr.dto.search.SearchRecruitmentRequestItemDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RecruitmentRequestItemService extends GenericService<RecruitmentRequestItem, UUID> {
    RecruitmentRequestItemDto saveOrUpdate(RecruitmentRequestItemDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    RecruitmentRequestItemDto getById(UUID id);

    Page<RecruitmentRequestItemDto> searchByPage(SearchRecruitmentRequestItemDto dto);

}
