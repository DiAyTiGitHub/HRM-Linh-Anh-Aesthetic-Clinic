package com.globits.hr.rest;

import com.globits.hr.dto.KPIItemDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.KPIItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kpi-item")
public class RestKPIItemController {

    @Autowired
    private KPIItemService kpiItemService;

    @RequestMapping(path = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<KPIItemDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<KPIItemDto> page = kpiItemService.paging(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
