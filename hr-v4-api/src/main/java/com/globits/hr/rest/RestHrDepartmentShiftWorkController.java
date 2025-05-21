package com.globits.hr.rest;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.service.HrDepartmentShiftWorkService;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hr-department-shift-work")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrDepartmentShiftWorkController {
    @Autowired
    private HrDepartmentShiftWorkService hrDepartmentShiftWorkService;


    @GetMapping("/shift-work-of-department/{id}")
    public ResponseEntity<List<ShiftWorkDto>> getShiftWorksOfDepartment(@PathVariable UUID id) {
        List<ShiftWorkDto> response = hrDepartmentShiftWorkService.getShiftWorksOfDepartment(id);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/department-has-shift-work/{id}")
    public ResponseEntity<List<HRDepartmentDto>> getDepartmentsHasShiftWork(@PathVariable UUID id) {
        List<HRDepartmentDto> response = hrDepartmentShiftWorkService.getDepartmentsHasShiftWork(id);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
