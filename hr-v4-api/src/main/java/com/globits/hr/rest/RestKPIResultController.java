package com.globits.hr.rest;

import com.globits.hr.dto.KPIResultDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.KPIResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/kpi-result")
public class RestKPIResultController {
    @Autowired
    private KPIResultService kpiResultService;

    @RequestMapping(method = RequestMethod.POST, path = "/save")
    public ResponseEntity<KPIResultDto> saveOrUpdate(@RequestBody KPIResultDto dto) {
        KPIResultDto response = kpiResultService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<KPIResultDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<KPIResultDto> page = kpiResultService.paging(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<KPIResultDto> getById(@PathVariable("id") UUID id) {
        KPIResultDto response = kpiResultService.getKPIById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean response = kpiResultService.deleteById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
