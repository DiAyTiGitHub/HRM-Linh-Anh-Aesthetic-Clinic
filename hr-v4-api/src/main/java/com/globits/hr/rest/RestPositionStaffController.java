package com.globits.hr.rest;

import com.globits.hr.dto.PositionStaffDto;
import com.globits.hr.dto.search.PositionStaffSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.PositionStaffService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/position-staff")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestPositionStaffController {
    @Resource
    private PositionStaffService service;

    @GetMapping(value = "/{id}")
    public PositionStaffDto getById(@PathVariable("id") UUID id) {
        return service.getPositionStaff(id);
    }

    @PostMapping(value = "/save")
    public PositionStaffDto savePositionStaff(@RequestBody PositionStaffDto dto) {
        return service.saveOrUpdate(dto);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean deletePositionStaff(@PathVariable("id") UUID id) {
        return service.deletePositionStaff(id);
    }

    @PostMapping(value = "/paging")
    public Page<PositionStaffDto> paging(@RequestBody PositionStaffSearchDto dto) {
        return service.paging(dto);
    }
}
