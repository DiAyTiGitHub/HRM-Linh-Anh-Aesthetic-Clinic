package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.dto.SalaryAreaDto;
import com.globits.salary.dto.SalaryAutoMapDto;
import com.globits.salary.dto.SalaryUnitDto;
import com.globits.salary.service.SalaryAreaService;
import com.globits.salary.service.SalaryAutoMapService;

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
@RequestMapping("/api/salary-auto-map")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestSalaryAutoMapController {
	
    @Autowired
    SalaryAutoMapService salaryAutoMapService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save-or-update",method = RequestMethod.POST)
    public ResponseEntity<SalaryAutoMapDto> saveSalaryArea(@RequestBody SalaryAutoMapDto dto){
    	SalaryAutoMapDto result = salaryAutoMapService.saveOrUpdate(dto);
        if(result == null){return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);}
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryAutoMapDto> getSalaryAutoMapDto(@PathVariable UUID id) {
    	SalaryAutoMapDto result = salaryAutoMapService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/get-all", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalaryAutoMapDto>> getAll(@RequestBody SearchDto searchDto) {
    	List<SalaryAutoMapDto> listData = salaryAutoMapService.getAll(searchDto);
        if (listData == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(listData, HttpStatus.OK);
    }
}
