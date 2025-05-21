package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;

public interface CalculateStaffWorkTimeServiceV2 extends GenericService<StaffWorkSchedule, UUID> {
    // Tính toán lại/Thống kê lại lịch làm việc trong khoảng thời được chọn
    List<UUID> reStatisticSchedulesInRangeTime(SearchStaffWorkScheduleDto dto);

    // Tính toán số giờ công của ca làm việc
    StaffWorkScheduleDto calculateStaffWorkTimeAndSave(UUID staffWorkScheduleId);

    // Lưu kết quả tính toán lại của ca làm việc
    StaffWorkScheduleDto saveScheduleStatistic(StaffWorkScheduleDto dto);

    // Thống kê các chỉ số của ca làm việc
    StaffWorkScheduleDto calculateStaffWorkTime(StaffWorkScheduleDto staffWorkSchedule,
                                                List<TimeSheetDetailDto> timeSheetDetails);

    // Thống kê kết quả làm việc trong trường hợp thông thường
    StaffWorkScheduleDto calculateStaffWorkTimeInNormalCase(StaffWorkScheduleDto staffWorkSchedule,
                                                            List<TimeSheetDetailDto> timeSheetDetails);

    // Thống kê kết quả làm việc trong trường hợp nghỉ nửa ca
    StaffWorkScheduleDto calculateStaffWorkTimeInHalfLeaveCase(StaffWorkScheduleDto staffWorkSchedule,
                                                               List<TimeSheetDetailDto> timeSheetDetails);

    // Thống kê kết quả làm việc trong trường hợp nghỉ cả ca
    StaffWorkScheduleDto calculateStaffWorkTimeInFullLeaveCase(StaffWorkScheduleDto staffWorkSchedule,
                                                               List<TimeSheetDetailDto> timeSheetDetails);

    // Map các lần chấm công với ca làm việc mới được phân
    StaffWorkScheduleDto mapOrphanedTimesheetDetailInDayToSchedule(UUID scheduleId);
}
