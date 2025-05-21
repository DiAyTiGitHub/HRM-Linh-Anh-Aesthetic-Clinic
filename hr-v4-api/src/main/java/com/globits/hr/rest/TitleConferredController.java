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
import com.globits.hr.dto.TitleConferredDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.TitleConferredService;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/titleConferred")
public class TitleConferredController {
	    @Autowired
	    private TitleConferredService titleConferredService;

	    @Secured("ROLE_ADMIN")
	    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    public ResponseEntity<TitleConferredDto> getTitleConferred(@PathVariable UUID id) {
	    	TitleConferredDto result = titleConferredService.getTitleConferred(id);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
	        Boolean result = titleConferredService.deleteTitleConferred(id);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
	    public ResponseEntity<Page<TitleConferredDto>> searchByPage(@RequestBody SearchDto searchDto) {
	        Page<TitleConferredDto> page = this.titleConferredService.searchByPage(searchDto);
	        return new ResponseEntity<>(page, HttpStatus.OK);
	    }

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
	    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
	                                             @RequestParam("code") String code) {
	        Boolean result = titleConferredService.checkCode(id, code);
	        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
	    }
	    @Secured({HrConstants.ROLE_HR_MANAGEMENT,Constants.ROLE_ADMIN})
		@RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
		public ResponseEntity<Page<TitleConferredDto>> getPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
			// SimpleFilterProvider filterProvider = new SimpleFilterProvider();
			Page<TitleConferredDto> results = titleConferredService.getPage(pageSize, pageIndex);
			// SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1", "field2");
			return new ResponseEntity<Page<TitleConferredDto>>(results, HttpStatus.OK);
		}

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(method = RequestMethod.POST)
	    public ResponseEntity<TitleConferredDto> save(@RequestBody TitleConferredDto dto) {
	    	TitleConferredDto result = titleConferredService.saveOrUpdate(null, dto);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }

	    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
	    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	    public ResponseEntity<TitleConferredDto> update(@RequestBody TitleConferredDto dto, @PathVariable UUID id) {
	    	TitleConferredDto result = titleConferredService.saveOrUpdate(id, dto);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    }
}
