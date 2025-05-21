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
import com.globits.hr.dto.EducationalManagementLevelDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.EducationalManagementLevelService;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/educationalManagementLevel")
public class RestEducationalManagementLevelController {
	 @Autowired
	    private EducationalManagementLevelService educationalManagementLevelService;

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(method = RequestMethod.POST)
	    public ResponseEntity<EducationalManagementLevelDto> saveEducationalManagementLevel(@RequestBody EducationalManagementLevelDto dto) {
	    	EducationalManagementLevelDto result = educationalManagementLevelService.saveEducationalManagementLevel(dto);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

	    @Secured("ROLE_ADMIN")
	    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    public ResponseEntity<EducationalManagementLevelDto> getEducationalManagementLevel(@PathVariable UUID id) {
	    	EducationalManagementLevelDto result = educationalManagementLevelService.getEducationalManagementLevel(id);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	    public ResponseEntity<Boolean> deleteEducationalManagementLevel(@PathVariable UUID id) {
	        Boolean result = educationalManagementLevelService.deleteEducationalManagementLevel(id);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

//	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//	    @RequestMapping(value = "", method = RequestMethod.PUT)
//	    public EducationalManagementQualificationsDto updateEducationalManagementQualifications(@RequestBody EducationalManagementQualificationsDto dto) {
//	        return educationalManagementQualificationsService.updateEducationalManagementQualifications(dto);
//	    }
//	    
	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
	    public EducationalManagementLevelDto update(@RequestBody EducationalManagementLevelDto dto, @PathVariable UUID id) {
	        return educationalManagementLevelService.updateEducationalManagementLevel(dto,id);
	    }

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
	    public ResponseEntity<Page<EducationalManagementLevelDto>> searchByPage(@RequestBody SearchDto searchDto) {
	        Page<EducationalManagementLevelDto> page = this.educationalManagementLevelService.searchByPage(searchDto);
	        return new ResponseEntity<>(page, HttpStatus.OK);
	    }

//	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//	    @RequestMapping(value = "/check/codeWasUsed", method = RequestMethod.POST)
//	    public ResponseEntity<Boolean> checkCode(@RequestBody EducationalManagementQualificationsDto dto) {
//	        Boolean result = educationalManagementQualificationsService.checkCode(dto.getId(), dto.getCode());
//	        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
//	    }
	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
	    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
	                                             @RequestParam("code") String code) {
	        Boolean result = educationalManagementLevelService.checkCode(id, code);
	        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
	    }

}
