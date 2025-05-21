package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.hr.dto.StaffWorkScheduleListDto;
import com.globits.hr.dto.function.Interval;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface CalculateStaffWorkTimeService extends GenericService<StaffWorkSchedule, UUID> {
    // Thống kê các chỉ số của ca làm việc
    StaffWorkScheduleDto calculateStaffWorkTime(UUID staffWorkScheduleId);

    // Thống kê với trường hợp chỉ chấm công vào ra 1 lần
    StaffWorkSchedule calculateStaffWorkTimeWithOnlyOneEntry(UUID staffWorkScheduleId);

    // Thống kê với trường hợp chấm công vào ra nhiều lần
    StaffWorkSchedule calculateStaffWorkTimeWithMultipleEntriesV2(UUID staffWorkScheduleId);

    // Tính số giờ nghỉ làm được hưởng lương trong ca làm việc
    double calculatePaidLeaveHours(List<LeaveRequest> leaveRequests, ShiftWork shiftWork, Date workingDate);

    // Các khoảng thực tế nhân viên đã làm việc từ dữ liệu chấm công
    // sau khi đã trừ các khoảng xin nghỉ được phê duyệt
    List<Interval> getValidWorkIntervals(
            List<TimeSheetDetail> timeSheetDetails,
            ShiftWork shiftWork,
            List<LeaveRequest> approvedLeaveRequests,
            Date workingDate
    );

    // Tính thời gian làm việc ước tính
    // sau khi đã loại bỏ các khoảng thời gian xin nghỉ
    double getEstimatedWorkingHours(
            StaffWorkSchedule entity,
            List<LeaveRequest> availableRequests
    );

}
