package com.globits.timesheet.service;

import com.globits.hr.dto.function.ProjectTimeWorkChartDto;
import com.globits.hr.dto.function.ProjectTimeWorkTableDto;
import com.globits.timesheet.dto.search.SearchTotalTimeReportDto;

import org.springframework.data.domain.Page;

public interface ProjectTimeWorkTableService {
    Page<ProjectTimeWorkTableDto> getPageAllStaff();

    Page<ProjectTimeWorkTableDto> getPageStaff(SearchTotalTimeReportDto dto);

    ProjectTimeWorkChartDto getChartTimeWork(SearchTotalTimeReportDto dto);
}
