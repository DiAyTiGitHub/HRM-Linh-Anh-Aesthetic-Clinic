package com.globits.hr.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StateManagementLevelDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.StateManagementLevelService;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/stateManagementLevel")
public class RestStateManagementLevelController {
	 @Autowired
	    private StateManagementLevelService stateManagementLevelService;

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(method = RequestMethod.POST)
	    public ResponseEntity<StateManagementLevelDto> saveStateManagementLevel(@RequestBody StateManagementLevelDto dto) {
	    	StateManagementLevelDto result = stateManagementLevelService.saveStateManagementLevel(dto);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

	    @Secured("ROLE_ADMIN")
	    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    public ResponseEntity<StateManagementLevelDto> getStateManagementLevel(@PathVariable UUID id) {
	    	StateManagementLevelDto result = stateManagementLevelService.getStateManagementLevel(id);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	    public ResponseEntity<Boolean> deleteStateManagementQualifications(@PathVariable UUID id) {
	        Boolean result = stateManagementLevelService.deleteStateManagementLevel(id);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

//	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//	    @RequestMapping(value = "", method = RequestMethod.PUT)
//	    public StateManagementQualificationsDto updateStateManagementQualifications(@RequestBody StateManagementQualificationsDto dto) {
//	        return stateManagementQualificationsService.updateStateManagementQualifications(dto);
//	    }
	    
	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
	    public StateManagementLevelDto update(@RequestBody StateManagementLevelDto dto, @PathVariable UUID id) {
	        return stateManagementLevelService.updateStateManagementLevel(dto,id);
	    }

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
	    public ResponseEntity<Page<StateManagementLevelDto>> searchByPage(@RequestBody SearchDto searchDto) {
	        Page<StateManagementLevelDto> page = this.stateManagementLevelService.searchByPage(searchDto);
	        return new ResponseEntity<>(page, HttpStatus.OK);
	    }
//
//	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//	    @RequestMapping(value = "/check/codeWasUsed", method = RequestMethod.POST)
//	    public ResponseEntity<Boolean> checkCode(@RequestBody StateManagementQualificationsDto dto) {
//	        Boolean result = stateManagementQualificationsService.checkCode(dto.getId(), dto.getCode());
//	        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
//	    }
	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
	    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
	                                             @RequestParam("code") String code) {
	        Boolean result = stateManagementLevelService.checkCode(id, code);
	        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
	    }
}
