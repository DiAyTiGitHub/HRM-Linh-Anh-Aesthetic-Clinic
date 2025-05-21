package com.globits.hr.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrEducationTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.HrEducationTypeService;


@RestController
@RequestMapping("/api/hrEducationType")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrEducationTypeController {
    @Autowired
    private HrEducationTypeService hrEducationTypeService;

    @PostMapping("/searchByPage")
    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_USER", "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT"})
    public Page<HrEducationTypeDto> searchByPage(@RequestBody SearchDto dto) {
        return hrEducationTypeService.searchByPage(dto);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_USER", "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT"})
    @GetMapping(value = "/{id}")
    public HrEducationTypeDto getEducationType(@PathVariable String id) {
        return hrEducationTypeService.getHrEducationType(UUID.fromString(id));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping()
    public ResponseEntity<HrEducationTypeDto> create(@RequestBody HrEducationTypeDto dto) {
        HrEducationTypeDto result = hrEducationTypeService.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{id}")
    public ResponseEntity<HrEducationTypeDto> update(@RequestBody HrEducationTypeDto dto, @PathVariable UUID id) {
        HrEducationTypeDto result = hrEducationTypeService.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = hrEducationTypeService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
        hrEducationTypeService.remove(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
