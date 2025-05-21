package com.globits.timesheet.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.timesheet.dto.PublicHolidayDateDto;
import com.globits.timesheet.dto.search.SearchPublicHolidayDateDto;
import com.globits.timesheet.service.PublicHolidayDateService;
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
@RequestMapping(value = "/api/public-holiday-date")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestPublicHolidayDateController {

    @Autowired
    private PublicHolidayDateService publicHolidayDateService;

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @PostMapping("/save-or-update")
    public ResponseEntity<PublicHolidayDateDto> savePublicHolidayDate(@RequestBody PublicHolidayDateDto publicHolidayDateDto) {
    	PublicHolidayDateDto result = publicHolidayDateService.saveOrUpdate(publicHolidayDateDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @PostMapping("/create-automatic")
    public ResponseEntity<Boolean> createPublicHolidayDateAutomatic(@RequestBody SearchDto publicHolidayDateDto) {
    	Boolean result = publicHolidayDateService.createPublicHolidayDateAutomatic(publicHolidayDateDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<PublicHolidayDateDto> getShiftRegistrationById(@PathVariable UUID id) {
    	PublicHolidayDateDto result = publicHolidayDateService.getPublicHolidayDateById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
  
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletePublicHolidayDate(@PathVariable UUID id) {
        Boolean result = publicHolidayDateService.deleteById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @PostMapping("/search-by-page")
    public ResponseEntity<Page<PublicHolidayDateDto>> pagingPublicHolidayDate(@RequestBody SearchPublicHolidayDateDto searchDto) {
        Page<PublicHolidayDateDto> result = publicHolidayDateService.pagingPublicHolidayDate(searchDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
	@RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
		Boolean deleted = publicHolidayDateService.deleteMultiple(ids);
		if (deleted) {
			return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
		}
	}


}
