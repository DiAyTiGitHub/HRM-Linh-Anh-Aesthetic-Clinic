package com.globits.timesheet.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.timesheet.domain.ShiftRegistration;
import com.globits.timesheet.dto.search.SearchShiftRegistrationDto;
import com.globits.timesheet.dto.ShiftRegistrationDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ShiftRegistrationService extends GenericService<ShiftRegistration, UUID> {
    ShiftRegistrationDto getShiftRegistration(UUID id);

    ShiftRegistration getEntityById(UUID id);

    Boolean deleteById(UUID id);
    
    int markDelete(UUID id);

    ShiftRegistrationDto saveOrUpdate(ShiftRegistrationDto dto);

    Page<ShiftRegistrationDto> pagingShiftRegistrations(SearchShiftRegistrationDto dto);

	List<UUID> updateApprovalStatus(SearchShiftRegistrationDto searchDto);

    StaffWorkScheduleDto createStaffWorkSchedule(ShiftRegistrationDto shiftRegistrationDto);

    String createStaffWorkSchedules(List<ShiftRegistrationDto> listShiftRegistrationDto);
}
