package com.globits.timesheet.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.globits.hr.dto.function.TotalTimeReportDto;
import com.globits.timesheet.dto.search.SearchTotalTimeReportDto;
@Service
public interface TotalTimeReportService {
	Page<TotalTimeReportDto> totalTimeReport(SearchTotalTimeReportDto dto);
}
