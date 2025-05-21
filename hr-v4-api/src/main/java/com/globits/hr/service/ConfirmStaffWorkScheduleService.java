package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.hr.dto.StaffWorkScheduleListDto;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.ShiftChangeRequest;
import com.globits.timesheet.dto.OvertimeRequestDto;
import com.globits.timesheet.dto.ShiftChangeRequestDto;
import com.globits.timesheet.dto.search.SearchOvertimeRequestDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.dto.search.ShiftChangeRequestSearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ConfirmStaffWorkScheduleService extends GenericService<StaffWorkSchedule, UUID> {
    List<UUID> updateApprovalStatus(AbsenceRequestSearchDto searchDto);

    Page<StaffWorkScheduleDto> pagingConfirmStaffWorkSchedule(SearchOvertimeRequestDto dto);

    SearchOvertimeRequestDto getInitialFilter();
}
