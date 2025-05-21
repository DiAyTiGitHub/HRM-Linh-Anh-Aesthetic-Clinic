package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.AllowancePolicyDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.AllowancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/allowance-policy")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestAllowancePolicyController {
    @Autowired
    AllowancePolicyService allowancePolicyService;
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/search-by-page")
    public Page<AllowancePolicyDto> searchByPage(@RequestBody SearchDto dto) {
        return allowancePolicyService.searchByPage(dto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/{id}")
    public AllowancePolicyDto getAllowanceType(@PathVariable String id) {
        return allowancePolicyService.getAllowancePolicyById(UUID.fromString(id));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
    	allowancePolicyService.deleteAllowancePolicy(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/save-or-update")
    public ResponseEntity<AllowancePolicyDto> saveOrUpdate(@RequestBody AllowancePolicyDto dto) {
        AllowancePolicyDto response = allowancePolicyService.saveOrUpdate(dto, dto.getId());
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
