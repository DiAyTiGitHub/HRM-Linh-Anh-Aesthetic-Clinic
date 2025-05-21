package com.globits.hr.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
// import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.AcademicTitleDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.AcademicTitleService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/academicTitle")
public class RestAcademicTitleController {
    @Autowired
    private AcademicTitleService academicTitleService;

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<AcademicTitleDto> getAcademicTitle(@PathVariable UUID id) {
        AcademicTitleDto result = academicTitleService.getAcademicTitle(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        Boolean result = academicTitleService.deleteAcademicTitle(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<AcademicTitleDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<AcademicTitleDto> page = this.academicTitleService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = academicTitleService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    @Secured({HrConstants.ROLE_HR_MANAGEMENT,Constants.ROLE_ADMIN})
	@RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
	public ResponseEntity<Page<AcademicTitleDto>> getPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
		// SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		Page<AcademicTitleDto> results = academicTitleService.getPage(pageSize, pageIndex);
		// SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1", "field2");
		return new ResponseEntity<Page<AcademicTitleDto>>(results, HttpStatus.OK);
	}

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AcademicTitleDto> save(@RequestBody AcademicTitleDto dto) {
        AcademicTitleDto result = academicTitleService.saveOrUpdate(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AcademicTitleDto> update(@RequestBody AcademicTitleDto dto, @PathVariable UUID id) {
        AcademicTitleDto result = academicTitleService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
