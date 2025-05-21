package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.AllowanceDto;
import com.globits.hr.dto.SearchAllowanceDto;
import com.globits.hr.service.AllowanceService;
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
@RequestMapping("/api/allowance")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestAllowanceController {
    @Autowired
    AllowanceService allowanceService;
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/searchByPage")
    public Page<AllowanceDto> searchByPage(@RequestBody SearchAllowanceDto dto) {
        return allowanceService.searchByPage(dto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/{id}")
    public AllowanceDto getAllowanceType(@PathVariable String id) {
        return allowanceService.getAllowance(UUID.fromString(id));
    }
    

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
        allowanceService.deleteAllowance(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @PostMapping(value = "/delete-multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
		Boolean deleted = allowanceService.deleteMultiple(ids);
		if (deleted) {
			return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
		}
	}
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/save-or-update")
    public ResponseEntity<AllowanceDto> saveOrUpdate(@RequestBody AllowanceDto dto) {
        // RecruitmentPlan's code is duplicated
        Boolean isValidCode = allowanceService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<AllowanceDto>(dto, HttpStatus.CONFLICT);
        }
        AllowanceDto response = allowanceService.saveOrUpdate(dto, dto.getId());
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
