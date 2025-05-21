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

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ProfessionalDegreeDto;
import com.globits.hr.dto.search.SearchDto;
/*
 *
 */
import com.globits.hr.service.ProfessionalDegreeService;

@RestController
@RequestMapping("/api/professionalDegree")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestProfessionalDegreeController {
    @Autowired
    private ProfessionalDegreeService professionalDegreeService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ProfessionalDegreeDto> saveProfessionalDegree(@RequestBody ProfessionalDegreeDto dto) {
        ProfessionalDegreeDto result = professionalDegreeService.saveProfessionalDegree(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProfessionalDegreeDto> getProfessionalDegree(@PathVariable UUID id) {
        ProfessionalDegreeDto result = professionalDegreeService.getProfessionalDegree(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteProfessionalDegree(@PathVariable UUID id) {
        Boolean result = professionalDegreeService.deleteProfessionalDegree(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ProfessionalDegreeDto updateProfessionalDegree(@RequestBody ProfessionalDegreeDto dto,
                                                          @PathVariable UUID id) {
        return professionalDegreeService.updateProfessionalDegree(dto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<ProfessionalDegreeDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<ProfessionalDegreeDto> page = this.professionalDegreeService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = professionalDegreeService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
