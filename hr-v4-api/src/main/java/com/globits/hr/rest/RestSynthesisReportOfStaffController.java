package com.globits.hr.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globits.hr.dto.SearchReportDto;
import com.globits.hr.dto.SynthesisReportOfStaffDto;
import com.globits.timesheet.service.TimeSheetService;

@RestController
@RequestMapping("/api/synthesisReportOfStaff")
public class RestSynthesisReportOfStaffController {
    @Autowired
    private TimeSheetService timeSheetService;

    @Secured({"ROLE_ADMIN", "ROLE_STAFF"})
    @RequestMapping(value = "/searchByDto/reportWorkingStatus/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    public ResponseEntity<Page<SynthesisReportOfStaffDto>> reportWorkingStatus(@RequestBody SearchReportDto searchReportDto, @PathVariable int pageIndex, @PathVariable int pageSize) {
        Page<SynthesisReportOfStaffDto> result = timeSheetService.reportWorkingStatus(searchReportDto, pageIndex, pageSize);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
