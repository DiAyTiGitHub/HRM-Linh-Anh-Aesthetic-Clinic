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
import com.globits.hr.dto.CivilServantCategoryGradeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.CivilServantCategoryGradeService;

/*
 * Quản lý ngạch bậc công chức (ngạch + bậc => một hệ số lương)
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/civilServantCategoryGrade")
public class RestCivilServantCategoryGradeController {
    @Autowired
    private CivilServantCategoryGradeService civilServantCategoryGradeService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public CivilServantCategoryGradeDto create(@RequestBody CivilServantCategoryGradeDto civilServantCategoryGradeDto) {
        return civilServantCategoryGradeService.saveOrUpdate(null, civilServantCategoryGradeDto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CivilServantCategoryGradeDto> update(@RequestBody CivilServantCategoryGradeDto dto, @PathVariable UUID id) {
        CivilServantCategoryGradeDto result = civilServantCategoryGradeService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CivilServantCategoryGradeDto> getOne(@PathVariable("id") UUID id) {
        CivilServantCategoryGradeDto dto = civilServantCategoryGradeService.getCivilServantCategoryGrade(id);
        if (dto == null) {
            return new ResponseEntity<>(new CivilServantCategoryGradeDto(),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{civilServantCategoryGradeId}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable("civilServantCategoryGradeId") String civilServantCategoryId) {
        return civilServantCategoryGradeService.removeCivilServantCategoryGrade(UUID.fromString(civilServantCategoryId));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<CivilServantCategoryGradeDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<CivilServantCategoryGradeDto> page = this.civilServantCategoryGradeService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id, @RequestParam("code") String code) {
        Boolean result = civilServantCategoryGradeService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
