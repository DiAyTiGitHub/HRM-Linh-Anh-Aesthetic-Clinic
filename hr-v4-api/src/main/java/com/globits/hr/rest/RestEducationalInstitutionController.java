package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.EducationalInstitutionDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.EducationalInstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/educationalInstitution")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestEducationalInstitutionController {
    @Autowired
    EducationalInstitutionService educationalInstitutionService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping()
    public ResponseEntity<EducationalInstitutionDto> create(@RequestBody EducationalInstitutionDto dto) {
        EducationalInstitutionDto result = educationalInstitutionService.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{id}")
    public ResponseEntity<EducationalInstitutionDto> update(@RequestBody EducationalInstitutionDto dto, @PathVariable UUID id) {
        EducationalInstitutionDto result = educationalInstitutionService.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = educationalInstitutionService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
        educationalInstitutionService.remove(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/searchByPage")
    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_USER", "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT"})
    public Page<EducationalInstitutionDto> searchByPage(@RequestBody SearchDto dto) {
        return educationalInstitutionService.searchByPage(dto);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_USER", "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT"})
    @GetMapping(value = "/{id}")
    public EducationalInstitutionDto getEducationalInstitution(@PathVariable String id) {
        return educationalInstitutionService.getEducationalInstitution(UUID.fromString(id));
    }

}
