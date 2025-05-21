package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.*;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.TimeSheet;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.TimeSheetStaffDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface StaffWorkScheduleService extends GenericService<StaffWorkSchedule, UUID> {
    // Phân 1 nhiều ca làm việc cho 1 nhiều nhân viên
    StaffWorkScheduleListDto assignShiftForMultipleStaffs(StaffWorkScheduleListDto dto);

    // Lấy danh sách ca làm việc trong ngày cụ thể của nhân viên
    List<StaffWorkScheduleDto> getSchedulesInDayOfStaff(SearchStaffWorkScheduleDto dto);

    // Tự động lấy thông tin phân ca làm việc theo người dùng hiện tại
    StaffWorkScheduleListDto getInitialShiftAssignmentForm();

    StaffWorkScheduleDto saveOrUpdate(StaffWorkScheduleDto dto);

    Page<StaffWorkScheduleDto> searchByPage(SearchStaffWorkScheduleDto dto);


    StaffWorkScheduleDto getById(UUID id);

    Boolean deleteStaffWorkSchedule(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    // Danh sách Lịch làm việc có thể được xác nhận OT
    Page<StaffWorkScheduleDto> pagingOverTimeSchedules(SearchStaffWorkScheduleDto dto);

    StaffWorkScheduleDto updateScheduleOTHours(StaffWorkScheduleDto scheduleDto);

    List<StaffWorkSchedule> changeStatusFromLeaveRequest(LeaveRequest leaveRequest);

    Integer saveListImportExcel(List<StaffWorkScheduleDto> list);

    ApiResponse<List<StaffWorkScheduleDto>> generateAndMarkSchedulesFromApprovedRequest(UUID leaveRequestId, Integer status);


    TotalStaffWorkScheduleDto getStaffWorkScheduleSummary(SearchStaffWorkScheduleDto dto);

    List<StaffWorkScheduleDto> generateFixSchedulesInRangeTimeForStaff(UUID staffId, Date fromDate, Date toDate);

    List<UUID> lockSchedules(List<UUID> scheduleIds);

    Integer saveMultiple(StaffWorkScheduleDto dto);

    StaffWorkSchedule generateScheduleFromTimeSheetStaffDto(TimeSheetStaffDto dto);

    StaffWorkSchedule generateScheduleFromTimesheetDetailDto(TimeSheetDetailDto dto, TimeSheetDetail entity);


    ByteArrayResource exportActualTimesheet(SearchStaffWorkScheduleDto dto);

    List<UUID> getAllStaffId(SearchStaffWorkScheduleDto dto);

    SearchStaffWorkScheduleDto getInitialFilter();

	List<StaffWorkScheduleDto> searchStaffWorkSchedulesForCRM(SearchStaffWorkScheduleDto dto);
}
