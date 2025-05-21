package com.globits.hr.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.CivilServantCategoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.CivilServantCategoryService;

/*
 * Quản lý ngạch công chức
 */
@RestController
@RequestMapping("/api/civilServantCategory")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestCivilServantCategoryController {
    @Autowired
    private CivilServantCategoryService civilServantCategoryService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CivilServantCategoryDto> getCivilServantCategory(@PathVariable("id") UUID id) {
        CivilServantCategoryDto dto = civilServantCategoryService.getCivilServantCategory(id);
        if (dto == null) {
            return new ResponseEntity<>(new CivilServantCategoryDto(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{civilServantCategoryId}", method = RequestMethod.DELETE)
    public Boolean deleteOne(@PathVariable("civilServantCategoryId") String civilServantCategoryId) {
        return civilServantCategoryService.removeCivilServantCategory(UUID.fromString(civilServantCategoryId));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<CivilServantCategoryDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<CivilServantCategoryDto> page = this.civilServantCategoryService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id, @RequestParam("code") String code) {
        Boolean result = civilServantCategoryService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkName(@RequestParam(value = "id", required = false) UUID id, @RequestParam("name") String name) {
        Boolean result = civilServantCategoryService.checkName(id, name);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CivilServantCategoryDto> create(@RequestBody CivilServantCategoryDto dto) {
        CivilServantCategoryDto result = civilServantCategoryService.saveOrUpdate(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CivilServantCategoryDto> update(@RequestBody CivilServantCategoryDto dto, @PathVariable("id") UUID id) {
        CivilServantCategoryDto result = civilServantCategoryService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
