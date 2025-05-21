package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.dto.SalaryParameterDto;
import com.globits.salary.service.SalaryParameterService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary-parameter")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestSalaryParameterController {
    @Autowired
    SalaryParameterService salaryParameterService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/saveSalaryParameter")
    public ResponseEntity<SalaryParameterDto> saveSalaryParameter(@RequestBody SalaryParameterDto dto) {
        SalaryParameterDto result = salaryParameterService.saveSalaryParameter(dto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public  ResponseEntity<SalaryParameterDto> getSalaryParameter(@PathVariable UUID id) {
        SalaryParameterDto result = salaryParameterService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteSalaryParameter(@PathVariable UUID id) {
        Boolean result = salaryParameterService.deleteSalaryParameter(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/deleteMultiple", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> listIds) {
        Boolean result = salaryParameterService.deleteMultipleSalaryParameters(listIds);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingSalaryParameter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SalaryParameterDto>> pagingSalaryParameter(@RequestBody SearchDto searchDto) {
        Page<SalaryParameterDto> page = salaryParameterService.pagingSalaryParameters(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);

    }

}
