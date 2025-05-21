package com.globits.timesheet.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.timesheet.dto.LabelDto;
import com.globits.timesheet.service.LabelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/label")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestLabelController {
    @Autowired
    private LabelService labelService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping(value = "/save-list")
    public ResponseEntity<List<LabelDto>> saveList(@RequestBody List<LabelDto> dto) {
        List<LabelDto> result = labelService.saveList(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/getAll")
    public ResponseEntity<List<LabelDto>> getAllLabel() {
        return new ResponseEntity<>(labelService.getAllLabel(), HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/getAll/{id}")
    public ResponseEntity<List<LabelDto>> getAllLabelByProjectId(@PathVariable UUID id) {
        return new ResponseEntity<>(labelService.getAllLabelByProjectId(id), HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping()
    public ResponseEntity<LabelDto> create(@RequestBody LabelDto dto) {
        LabelDto result = labelService.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PutMapping(value = "/{id}")
    public ResponseEntity<LabelDto> save(@RequestBody LabelDto dto, @PathVariable UUID id) {
        LabelDto result = labelService.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        Boolean result = labelService.deleteOne(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/{id}")
    public ResponseEntity<LabelDto> getCertificate(@PathVariable UUID id) {
        LabelDto result = labelService.getOneLabel(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
