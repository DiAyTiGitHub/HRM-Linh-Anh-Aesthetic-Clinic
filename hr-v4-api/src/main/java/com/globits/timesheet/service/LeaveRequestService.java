package com.globits.timesheet.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.ApiResponse;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.dto.LeaveRequestDto;
import com.globits.timesheet.dto.search.LeaveRequestSearchDto;

public interface LeaveRequestService extends GenericService<LeaveRequest, UUID> {
    LeaveRequestDto saveOrUpdate(LeaveRequestDto dto);

    LeaveRequestDto getLeaveRequest(UUID id);

    Page<LeaveRequestDto> searchByPage(LeaveRequestSearchDto dto);

    List<LeaveRequestDto> getAllLeaveRequestBySearchStaffWorkSchedule(SearchStaffWorkScheduleDto searchDto);

    LeaveRequestDto getById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    Boolean deleteById(UUID id);

    List<UUID> updateRequestsApprovalStatus(LeaveRequestSearchDto searchDto);

    ApiResponse<List<UUID>> isExistLeaveRequestInPeriod(List<UUID> staffIds, Date fromDate, Date toDate);

    XWPFDocument generateUnpaidLeaveDocx(UUID id) throws IOException;

    Integer saveListLeaveRequest(List<LeaveRequestDto> list);

    LeaveRequestSearchDto getInitialFilter();
}
