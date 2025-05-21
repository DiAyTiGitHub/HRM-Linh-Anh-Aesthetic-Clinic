package com.globits.hr.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.globits.hr.HrConstants;
import com.globits.hr.dto.HRDisciplineDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.HRDisciplineService;

@RestController
@RequestMapping("/api/hrDiscipline")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHRDisciplineController {
    @Autowired
    HRDisciplineService hrDisciplineService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, HrConstants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<HRDisciplineDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<HRDisciplineDto> page = hrDisciplineService.searchByPage(dto);
        return new ResponseEntity<>(page, page != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<HRDisciplineDto> getOne(@PathVariable("id") UUID id) {
        HRDisciplineDto result = hrDisciplineService.getOne(id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<HRDisciplineDto> create(@RequestBody HRDisciplineDto dto) {
        HRDisciplineDto result = hrDisciplineService.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, HrConstants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<HRDisciplineDto> update(@RequestBody HRDisciplineDto dto, @PathVariable("id") UUID id) {
        HRDisciplineDto result = hrDisciplineService.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, HrConstants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteOne(@PathVariable("id") UUID id) {
        hrDisciplineService.deleteOne(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, HrConstants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = hrDisciplineService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(hrDisciplineService.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
