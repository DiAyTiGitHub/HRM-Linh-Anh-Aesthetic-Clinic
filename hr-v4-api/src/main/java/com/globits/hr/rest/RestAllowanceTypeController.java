package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.AllowanceTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.AllowanceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/allowance-type")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestAllowanceTypeController {
    @Autowired
    AllowanceTypeService allowanceTypeService;

    @PostMapping("/searchByPage")
    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_USER", "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT"})
    public Page<AllowanceTypeDto> searchByPage(@RequestBody SearchDto dto) {
        return allowanceTypeService.searchByPage(dto);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_USER", "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT"})
    @GetMapping(value = "/{id}")
    public AllowanceTypeDto getAllowanceType(@PathVariable String id) {
        return allowanceTypeService.getAllowanceType(UUID.fromString(id));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping()
    public ResponseEntity<AllowanceTypeDto> create(@RequestBody AllowanceTypeDto dto) {
        Boolean checkCode = allowanceTypeService.checkCode(dto.getId(), dto.getCode());
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        AllowanceTypeDto result = allowanceTypeService.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{id}")
    public ResponseEntity<AllowanceTypeDto> update(@RequestBody AllowanceTypeDto dto, @PathVariable UUID id) {
        UUID currentId = dto.getId() != null ? dto.getId() : id;
        Boolean checkCode = allowanceTypeService.checkCode(currentId, dto.getCode());
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        AllowanceTypeDto result = allowanceTypeService.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = allowanceTypeService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
        allowanceTypeService.remove(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}
