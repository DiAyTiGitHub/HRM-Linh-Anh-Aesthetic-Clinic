package com.globits.hr.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.globits.core.Constants;
import com.globits.core.dto.CountryDto;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.HrCountryService;

@RestController
@RequestMapping("/api/hrCountry")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrCountryController {
    @Autowired
    private HrCountryService hrCountryService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping("/searchByPage")
    public ResponseEntity<Page<CountryDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<CountryDto> page = hrCountryService.searchByPage(dto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping()
    public ResponseEntity<CountryDto> create(@RequestBody CountryDto dto) {
        CountryDto result = hrCountryService.saveOne(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{id}")
    public ResponseEntity<CountryDto> update(@RequestBody CountryDto dto, @PathVariable UUID id) {
        CountryDto result = hrCountryService.saveOne(dto, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
        hrCountryService.remove(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping("/checkCode")
    public ResponseEntity<Boolean> checkCode(@RequestBody CountryDto dto) {
        Boolean result = hrCountryService.checkCode(dto.getId(), dto.getCode());
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @GetMapping("/get-by-code/{code}")
    public CountryDto findByCode(@PathVariable String code) {
        return hrCountryService.findByCode(code);
    }
}
