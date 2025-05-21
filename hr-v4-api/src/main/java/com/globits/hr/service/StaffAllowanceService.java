package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffAllowance;
import com.globits.hr.dto.StaffAllowanceDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffAllowanceService extends GenericService<StaffAllowance, UUID> {
    StaffAllowanceDto getStaffAllowanceById(UUID id);

//    StaffAllowanceDto saveOrUpdate(StaffAllowanceDto dto, UUID id);
    
    StaffAllowanceDto saveOrUpdate(StaffAllowanceDto dto);

    void deleteStaffAllowance(UUID id);
    
    Page<StaffAllowanceDto> searchByPage(SearchDto dto);

	List<StaffAllowanceDto> getStaffAllowanceByStaffId(UUID staffId);
}
