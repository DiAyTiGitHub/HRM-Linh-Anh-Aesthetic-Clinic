package com.globits.hr.rest;

import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.KPIDto;
import com.globits.hr.service.KPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/kpi")
public class RestKPIController {

    @Autowired
    private KPIService kpiService;

    @RequestMapping(method = RequestMethod.POST, path = "/save")
    public ResponseEntity<KPIDto> saveOrUpdate(@RequestBody KPIDto dto) {
        Boolean checkCode = kpiService.checkCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }  KPIDto response = kpiService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<KPIDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<KPIDto> page = kpiService.paging(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<KPIDto> getById(@PathVariable("id") UUID id) {
        KPIDto response = kpiService.getKPIById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean response = kpiService.deleteById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
