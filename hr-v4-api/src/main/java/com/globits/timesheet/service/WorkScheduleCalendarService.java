package com.globits.timesheet.service;

import com.globits.timesheet.dto.calendar.ScheduledStaffCalendarDto;
import com.globits.timesheet.dto.search.SearchWorkScheduleCalendarDto;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface WorkScheduleCalendarService {
	Page<ScheduledStaffCalendarDto> getWorkingScheduleByFilter(SearchWorkScheduleCalendarDto searchDto);

    ScheduledStaffCalendarDto getWorkCalendarOfStaff(SearchWorkScheduleCalendarDto searchDto);

    SearchWorkScheduleCalendarDto getInitialTimekeepingReportFilter();

    Page<ScheduledStaffCalendarDto> getTimekeepingReportByFitler(SearchWorkScheduleCalendarDto searchDto);

	Workbook handleExcel(SearchWorkScheduleCalendarDto dto);

}
