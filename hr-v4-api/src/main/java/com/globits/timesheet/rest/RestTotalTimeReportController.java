package com.globits.timesheet.rest;

import com.globits.hr.dto.function.TotalTimeReportDto;
import com.globits.timesheet.dto.search.SearchTotalTimeReportDto;
import com.globits.timesheet.service.TotalTimeReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/total-time-report")
public class RestTotalTimeReportController {
	@Autowired
	private TotalTimeReportService totalTimeReportService;

	@RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
	public ResponseEntity<Page<TotalTimeReportDto>> totalTimeReport(@RequestBody SearchTotalTimeReportDto searchDto) {
		Page<TotalTimeReportDto> page = totalTimeReportService.totalTimeReport(searchDto);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}
}
