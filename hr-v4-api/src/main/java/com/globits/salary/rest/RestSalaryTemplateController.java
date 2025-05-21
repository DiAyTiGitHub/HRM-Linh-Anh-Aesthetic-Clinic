package com.globits.salary.rest;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.search.SearchSalaryTemplateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.service.SalaryTemplateService;

@RestController
@RequestMapping("/api/salary-template")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestSalaryTemplateController {
    @Autowired
    private SalaryTemplateService salaryTemplateService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save-salary-template", method = RequestMethod.POST)
    public ResponseEntity<SalaryTemplateDto> saveSalaryTemplate(@RequestBody SalaryTemplateDto dto) {
        // SalaryTemplate's code is duplicated
        Boolean isValidCode = salaryTemplateService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<SalaryTemplateDto>(dto, HttpStatus.CONFLICT);
        }
        SalaryTemplateDto response = salaryTemplateService.saveSalaryTemplate(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/clon-salary-template", method = RequestMethod.POST)
    public ResponseEntity<SalaryTemplateDto> cloneSalaryTemplate(@RequestBody SalaryTemplateDto dto) {
        Boolean isValidCode = salaryTemplateService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<SalaryTemplateDto>(dto, HttpStatus.CONFLICT);
        }
        SalaryTemplateDto response = salaryTemplateService.clonSalaryTemplate(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryTemplateDto> getSalaryTemplate(@PathVariable UUID id) {
        SalaryTemplateDto result = salaryTemplateService.getSalaryTemplate(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteOne(@PathVariable UUID id) {
        Boolean result = salaryTemplateService.deleteSalaryTemplate(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/paging-salary-template", method = RequestMethod.POST)
    public ResponseEntity<Page<SalaryTemplateDto>> searchByPage(@RequestBody SearchSalaryTemplateDto searchDto) {
        Page<SalaryTemplateDto> response = this.salaryTemplateService.searchByPage(searchDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = salaryTemplateService.deleteMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }
}
