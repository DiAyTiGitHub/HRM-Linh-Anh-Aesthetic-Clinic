package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.core.dto.OrganizationDto;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.salary.domain.SalaryUnit;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryUnitDto;
import com.globits.salary.service.SalaryUnitService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary-unit")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestSalaryUnitController {
    @Autowired
    SalaryUnitService salaryUnitService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/saveSalaryUnit")
    public ResponseEntity<SalaryUnitDto> saveSalaryUnit(@RequestBody SalaryUnitDto dto) {
        Boolean checkCode = salaryUnitService.checkCode(dto.getId(), dto.getCode());
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        SalaryUnitDto result = salaryUnitService.saveOrUpdate(dto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteSalaryUnit(@PathVariable UUID id) {
        Boolean result = salaryUnitService.deleteSalaryUnit(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryUnitDto> getSalaryUnit(@PathVariable UUID id) {
        SalaryUnitDto result = salaryUnitService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id, @RequestParam("code") String code) {
        Boolean result = salaryUnitService.checkCode(id, code);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingSalaryUnit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SalaryUnitDto>> pagingSalaryUnit(@RequestBody SearchDto searchDto) {
        Page<SalaryUnitDto> page = salaryUnitService.searchByPage(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //deleteMultiple
    @RequestMapping(value = "/deleteMultiple", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> listIds) {
        Boolean result = salaryUnitService.deleteMultipleSalaryUnit(listIds);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
