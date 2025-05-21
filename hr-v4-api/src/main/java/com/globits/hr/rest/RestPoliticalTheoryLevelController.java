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
import com.globits.hr.dto.PoliticalTheoryLevelDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.PoliticalTheoryLevelService;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/politicalTheoryLevel")
public class RestPoliticalTheoryLevelController {
    @Autowired
    private PoliticalTheoryLevelService politicalTheoryLevelService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PoliticalTheoryLevelDto> savePoliticalTheoryLevel(@RequestBody PoliticalTheoryLevelDto dto) {
        PoliticalTheoryLevelDto result = politicalTheoryLevelService.savePoliticalTheoryLevel(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PoliticalTheoryLevelDto> getPoliticalTheoryLevel(@PathVariable UUID id) {
        PoliticalTheoryLevelDto result = politicalTheoryLevelService.getPoliticalTheoryLevel(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deletePoliticalTheoryLevel(@PathVariable UUID id) {
        Boolean result = politicalTheoryLevelService.deletePoliticalTheoryLevel(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//    @RequestMapping(value = "", method = RequestMethod.PUT)
//    public PoliticalTheoryLevelDto updatePoliticalTheoryLevel(@RequestBody PoliticalTheoryLevelDto dto) {
//        return politicalTheoryLevelService.updatePoliticalTheoryLevel(dto);
//    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public PoliticalTheoryLevelDto update(@RequestBody PoliticalTheoryLevelDto dto, @PathVariable UUID id) {
        return politicalTheoryLevelService.updatePoliticalTheoryLevel(dto,id);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<PoliticalTheoryLevelDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<PoliticalTheoryLevelDto> page = this.politicalTheoryLevelService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

//    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//    @RequestMapping(value = "/check/codeWasUsed", method = RequestMethod.POST)
//    public ResponseEntity<Boolean> checkCode(@RequestBody PoliticalTheoryLevelDto dto) {
//        Boolean result = politicalTheoryLevelService.checkCode(dto.getId(), dto.getCode());
//        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
//    }
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = politicalTheoryLevelService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
