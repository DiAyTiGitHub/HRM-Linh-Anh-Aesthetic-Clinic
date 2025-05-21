package com.globits.timesheet.rest;

import com.globits.hr.dto.function.ProjectTimeWorkChartDto;
import com.globits.hr.dto.function.ProjectTimeWorkTableDto;
import com.globits.timesheet.dto.search.SearchTotalTimeReportDto;
import com.globits.timesheet.service.ProjectTimeWorkTableService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project-time-work")
public class RestProjectTimeWorkTableController {
    @Autowired
    private ProjectTimeWorkTableService projectTimeWorkTableService;

    @RequestMapping(value = "/search-by-page", method = RequestMethod.GET)
    public ResponseEntity<Page<ProjectTimeWorkTableDto>> getPageAllStaff() {
        Page<ProjectTimeWorkTableDto> page = projectTimeWorkTableService.getPageAllStaff();
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/search-by-page-user", method = RequestMethod.POST)
    public ResponseEntity<Page<ProjectTimeWorkTableDto>> getPageStaff(@RequestBody SearchTotalTimeReportDto dto) {
        Page<ProjectTimeWorkTableDto> page = projectTimeWorkTableService.getPageStaff(dto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-chart-time", method = RequestMethod.POST)
    public ResponseEntity<ProjectTimeWorkChartDto> getChartTime(@RequestBody SearchTotalTimeReportDto dto) {
        ProjectTimeWorkChartDto result = projectTimeWorkTableService.getChartTimeWork(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
