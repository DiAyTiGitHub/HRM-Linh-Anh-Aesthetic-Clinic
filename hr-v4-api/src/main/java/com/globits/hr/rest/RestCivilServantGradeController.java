package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.CivilServantGradeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.CivilServantGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
 * Bậc công chức
 */
@RestController
@RequestMapping("/api/civilServantGrade")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestCivilServantGradeController {
    @Autowired
    private CivilServantGradeService civilServantGradeService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public CivilServantGradeDto create(@RequestBody CivilServantGradeDto civilServantGradeDto) {
        return civilServantGradeService.saveOrUpdate(null, civilServantGradeDto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CivilServantGradeDto> update(@RequestBody CivilServantGradeDto dto, @PathVariable UUID id) {
        CivilServantGradeDto result = civilServantGradeService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CivilServantGradeDto> getOne(@PathVariable("id") UUID id) {
        CivilServantGradeDto dto = civilServantGradeService.getCivilServantGrade(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{civilServantGradeId}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable("civilServantGradeId") String civilServantGradeId) {
        return civilServantGradeService.removeCivilServantGrade(UUID.fromString(civilServantGradeId));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<CivilServantGradeDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<CivilServantGradeDto> page = this.civilServantGradeService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id, @RequestParam("code") String code) {
        Boolean result = civilServantGradeService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
