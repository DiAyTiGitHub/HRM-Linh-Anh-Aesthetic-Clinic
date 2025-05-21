package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.dto.SalaryAreaDto;
import com.globits.salary.dto.SalaryUnitDto;
import com.globits.salary.service.SalaryAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary-area")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestSalaryAreaController {

    @Autowired
    SalaryAreaService salaryAreaService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/saveSalaryArea", method = RequestMethod.POST)
    public ResponseEntity<SalaryAreaDto> saveSalaryArea(@RequestBody SalaryAreaDto dto) {
        Boolean checkCode = salaryAreaService.checkCode(dto.getId(), dto.getCode());
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        SalaryAreaDto result = salaryAreaService.saveOrUpdateSalaryArea(dto);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteOne(@PathVariable UUID id) {
        Boolean result = salaryAreaService.deleteSalaryAres(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteMultiple", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> listIds) {
        Boolean result = salaryAreaService.deleteMultipleSalaryArea(listIds);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryAreaDto> getSalaryArea(@PathVariable UUID id) {
        SalaryAreaDto result = salaryAreaService.getSalaryAreaById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingSalaryArea", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SalaryAreaDto>> pagingSalaryArea(@RequestBody SearchDto searchDto) {
        Page<SalaryAreaDto> page = salaryAreaService.searchByPage(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
