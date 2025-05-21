package com.globits.hr.rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.globits.hr.dto.function.DashboardDto;
import com.globits.hr.dto.search.DashboardSearchDto;
import com.globits.hr.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(value = "*")
public class RestDashboardController {
    @Autowired
    DashboardService dashboardService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DashboardDto> getDashboardAnalytics(@RequestBody DashboardSearchDto dto) {
        DashboardDto result = dashboardService.getDashboard(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
