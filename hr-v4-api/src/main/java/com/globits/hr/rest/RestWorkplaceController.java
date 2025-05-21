package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.WorkplaceDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.WorkplaceService;

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
@RequestMapping("/api/workplace")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestWorkplaceController {
    @Autowired
    WorkplaceService workplaceService;
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/search-by-page")
    public Page<WorkplaceDto> searchByPage(@RequestBody SearchDto dto) {
        return workplaceService.searchByPage(dto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/{id}")
    public WorkplaceDto getWorkplace(@PathVariable String id) {
        return workplaceService.getWorkplaceById(UUID.fromString(id));
    }
    

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
        workplaceService.deleteWorkplace(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @PostMapping(value = "/delete-multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
		Boolean deleted = workplaceService.deleteMultiple(ids);
		if (deleted) {
			return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
		}
	}
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/save-or-update")
    public ResponseEntity<WorkplaceDto> saveOrUpdate(@RequestBody WorkplaceDto dto) {
        // RecruitmentPlan's code is duplicated
        Boolean isValidCode = workplaceService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<WorkplaceDto>(dto, HttpStatus.CONFLICT);
        }
        WorkplaceDto response = workplaceService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
