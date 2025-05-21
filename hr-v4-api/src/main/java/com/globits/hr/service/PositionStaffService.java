package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.OrganizationChartRelationDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.PositionStaffSearchDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.PositionStaff;
import com.globits.hr.dto.PositionStaffDto;
import com.globits.hr.dto.function.PositionTitleStaffDto;

@Service
public interface PositionStaffService extends GenericService<PositionStaff, UUID> {
	PositionStaffDto saveImportStaffEducationHistory(PositionTitleStaffDto dto);

//    PositionStaff mappingWithChartRelation(OrganizationChartRelationDto dto);

    List<PositionStaff> findPositionStaffByRelation(OrganizationChartRelationDto dto);

    PositionStaffDto getPositionStaff(UUID id);

    PositionStaffDto saveOrUpdate(PositionStaffDto dto);

    Boolean deletePositionStaff(UUID id);

    Page<PositionStaffDto> paging(PositionStaffSearchDto dto);

    List<StaffDto> getListStaffUnderManager(Long userId);
}
