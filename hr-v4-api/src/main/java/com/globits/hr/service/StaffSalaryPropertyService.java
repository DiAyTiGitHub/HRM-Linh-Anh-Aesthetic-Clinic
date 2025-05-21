package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.StaffSalaryPropertyDto;
import com.globits.salary.domain.SalaryItem;

public interface StaffSalaryPropertyService extends GenericService<SalaryItem, UUID> {
    Page<StaffSalaryPropertyDto> getPage(int pageSize, int pageIndex);

    Boolean deleteStaffSalaryProperty(UUID id);

    StaffSalaryPropertyDto saveStaffSalaryProperty(StaffSalaryPropertyDto dto);

    StaffSalaryPropertyDto getStaffSalaryProperty(UUID id);

    int deleteStaffSalaryPropertyList(List<StaffSalaryPropertyDto> dtoList);
}
