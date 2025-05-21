package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrGlobalPropertyDto;
import com.globits.hr.service.HrGlobalPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/global-property")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrGlobalPropertyController {
    @Autowired
    HrGlobalPropertyService globalPropertyService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping()
    public ResponseEntity<HrGlobalPropertyDto> create(@RequestBody HrGlobalPropertyDto dto) {
        HrGlobalPropertyDto result = globalPropertyService.saveGlobalProperty(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{property}")
    public ResponseEntity<HrGlobalPropertyDto> update(@RequestBody HrGlobalPropertyDto dto, @PathVariable String property) {
        HrGlobalPropertyDto result = globalPropertyService.saveGlobalProperty(dto, property);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @DeleteMapping("/{property}")
    public ResponseEntity<Boolean> remove(@PathVariable String property) {
        globalPropertyService.remove(property);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @GetMapping()
    public ResponseEntity<List<HrGlobalPropertyDto>> getAll() {
        List<HrGlobalPropertyDto> globalPropertyList = globalPropertyService.getList();
        return new ResponseEntity<>(globalPropertyList, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @GetMapping("/{property}")
    public ResponseEntity<HrGlobalPropertyDto> findGlobalProperty(@PathVariable String property) {
        HrGlobalPropertyDto globalProperty = globalPropertyService.findGlobalProperty(property);
        return new ResponseEntity<>(globalProperty, HttpStatus.OK);
    }
}
