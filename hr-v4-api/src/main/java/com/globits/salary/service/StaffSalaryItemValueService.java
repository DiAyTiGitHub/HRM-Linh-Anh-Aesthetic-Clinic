package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.StaffSalaryItemValue;
import com.globits.salary.dto.*;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffSalaryItemValueService extends GenericService<StaffSalaryItemValue, UUID> {

    StaffSalaryItemValueDto saveOrUpdateStaffSalaryItemValue(StaffSalaryItemValueDto dto);

    StaffSalaryItemValueDto preHandleHistoryAndSaveOrUpdate(StaffSalaryItemValueDto dto);

    StaffSalaryItemValueDto getById(UUID id);

    List<StaffSalaryItemValueDto> getSalaryValueHistories(UUID id);

    Boolean deleteStaffSalaryItemValue(UUID id);

    Page<StaffSalaryItemValueDto> pagingStaffSalaryItemValue(SearchDto dto);

    StaffSalaryItemValueListDto getBySalaryTemplateItem(RequestSalaryValueDto dto);

    Integer saveStaffSalaryItemValueList(StaffSalaryItemValueListDto dto);

    StaffSalaryItemValueDto getTaxBHXHByStaffId(UUID staffId);

    List<MapStaffSalaryItemValueDto> getByStaffId(UUID id);

    StaffSalaryItemValueDto save(StaffSalaryItemValueDto dto);

    StaffSalaryItemValue findCurrentByStaffIdAndSalaryItemId(UUID staffId, UUID salaryItemId);
}
