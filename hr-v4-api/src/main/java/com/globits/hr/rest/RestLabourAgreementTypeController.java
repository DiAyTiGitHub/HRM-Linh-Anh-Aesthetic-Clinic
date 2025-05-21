package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.LabourAgreementTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.LabourAgreementTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/labourAgreement")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestLabourAgreementTypeController {
    @Autowired
    private LabourAgreementTypeService labourAgreementService;

    @PostMapping(value = "/searchByPage")
    public ResponseEntity<Page<LabourAgreementTypeDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<LabourAgreementTypeDto> result = labourAgreementService.searchByPage(dto);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "")
    public ResponseEntity<LabourAgreementTypeDto> create(@RequestBody LabourAgreementTypeDto dto) {
        LabourAgreementTypeDto result = labourAgreementService.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<LabourAgreementTypeDto> update(@RequestBody LabourAgreementTypeDto dto,
                                                         @PathVariable("id") UUID id) {
        LabourAgreementTypeDto result = labourAgreementService.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LabourAgreementTypeDto> getOne(@PathVariable("id") UUID id) {
        LabourAgreementTypeDto result = labourAgreementService.getOne(id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deleteOne(@PathVariable("id") UUID id) {
        labourAgreementService.deleteOne(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = labourAgreementService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


}
