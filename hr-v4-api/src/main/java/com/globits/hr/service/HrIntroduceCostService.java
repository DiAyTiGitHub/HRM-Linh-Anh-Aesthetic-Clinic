package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.AllowancePolicy;
import com.globits.hr.domain.HrIntroduceCost;
import com.globits.hr.domain.Position;
import com.globits.hr.dto.AllowancePolicyDto;
import com.globits.hr.dto.HrIntroduceCostDto;
import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchHrIntroduceCostDto;
import com.globits.hr.dto.search.SearchPositionDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrIntroduceCostService extends GenericService<HrIntroduceCost, UUID> {
    Page<HrIntroduceCostDto> searchByPage(SearchHrIntroduceCostDto dto);

    HrIntroduceCostDto getById(UUID id);

    HrIntroduceCostDto saveOrUpdate(HrIntroduceCostDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    Workbook exportExcelIntroduceCost(SearchHrIntroduceCostDto dto);
}
