package com.globits.hr.rest;

// import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
// import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.LanguageDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/otherLanguage")
public class RestOtherLanguageController {
    @Autowired
    private LanguageService  otherLanguageService;

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<LanguageDto> getOtherLanguage(@PathVariable UUID id) {
        LanguageDto result =  otherLanguageService.getOtherLanguage(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        Boolean result =  otherLanguageService.deleteOtherLanguage(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<LanguageDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<LanguageDto> page = this.otherLanguageService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = otherLanguageService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    @Secured({HrConstants.ROLE_HR_MANAGEMENT,Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public ResponseEntity<Page<LanguageDto>> getPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
        // SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        Page<LanguageDto> results = otherLanguageService.getPage(pageSize, pageIndex);
        // SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1", "field2");
        return new ResponseEntity<Page<LanguageDto>>(results, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<LanguageDto> save(@RequestBody LanguageDto dto) {
        LanguageDto result =  otherLanguageService.saveOrUpdate(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity< LanguageDto> update(@RequestBody  LanguageDto dto, @PathVariable UUID id) {
        LanguageDto result =  otherLanguageService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
