package com.globits.hr.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.CivilServantTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.CivilServantTypeService;

@RestController
@RequestMapping("/api/civilServantType")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestCivilServantTypeController {
    @Autowired
    private CivilServantTypeService civilServantTypeService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public CivilServantTypeDto create(@RequestBody CivilServantTypeDto dto) {
        return civilServantTypeService.saveOrUpdate(dto, null);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public CivilServantTypeDto update(@RequestBody CivilServantTypeDto dto, @PathVariable("id") UUID id) {
        return civilServantTypeService.saveOrUpdate(dto, id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/searchByPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CivilServantTypeDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<CivilServantTypeDto> page = this.civilServantTypeService.searchByPage(searchDto);
        return new ResponseEntity<>(page, page != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") UUID id) {
        civilServantTypeService.deleteOne(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = civilServantTypeService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkName(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("name") String name) {
        Boolean result = civilServantTypeService.checkName(id, name);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CivilServantTypeDto> getOne(@PathVariable UUID id) {
        CivilServantTypeDto result = civilServantTypeService.getOne(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
