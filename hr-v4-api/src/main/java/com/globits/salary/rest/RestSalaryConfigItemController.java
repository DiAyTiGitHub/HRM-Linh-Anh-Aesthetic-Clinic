package com.globits.salary.rest;

/*
 * Giang và Tuấn Anh
 */

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.salary.dto.SalaryConfigItemDto;
import com.globits.salary.service.SalaryConfigItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/salaryConfigItem")
public class RestSalaryConfigItemController {
    @Autowired
    private SalaryConfigItemService salaryConfigItemService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{salaryConfigId}/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public ResponseEntity<Page<SalaryConfigItemDto>> getPageBySalaryConfigId(@PathVariable UUID salaryConfigId,
                                                                             @PathVariable int pageIndex, @PathVariable int pageSize) {
        Page<SalaryConfigItemDto> results = salaryConfigItemService.getPageBySalaryConfigId(salaryConfigId, pageSize,
                pageIndex);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryConfigItemDto> getSalaryConfigItem(@PathVariable UUID id) {
        SalaryConfigItemDto result = salaryConfigItemService.getSalaryConfigItem(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<SalaryConfigItemDto> saveSalaryConfigItem(@RequestBody SalaryConfigItemDto dto) {
        SalaryConfigItemDto result = salaryConfigItemService.saveSalaryConfigItem(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
