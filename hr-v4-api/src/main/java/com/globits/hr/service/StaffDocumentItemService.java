package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.HrIntroduceCost;
import com.globits.hr.domain.StaffDocumentItem;
import com.globits.hr.dto.HrIntroduceCostDto;
import com.globits.hr.dto.StaffDocumentItemDto;
import com.globits.hr.dto.TemplateStaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffDocumentItemDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffDocumentItemService extends GenericService<StaffDocumentItem, UUID> {
    Page<StaffDocumentItemDto> searchByPage(SearchStaffDocumentItemDto dto);

    StaffDocumentItemDto getById(UUID id);

    StaffDocumentItemDto saveOrUpdate(StaffDocumentItemDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    TemplateStaffDto getItemByTemplateStaff(SearchStaffDocumentItemDto searchDto);

    TemplateStaffDto saveTemplateStaff(TemplateStaffDto dto);
}
