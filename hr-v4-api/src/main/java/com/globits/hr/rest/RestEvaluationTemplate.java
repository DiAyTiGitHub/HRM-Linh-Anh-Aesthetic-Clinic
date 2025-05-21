package com.globits.hr.rest;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationTemplateDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.EvaluationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/evaluation-template")
public class RestEvaluationTemplate {

    @Autowired
    private EvaluationTemplateService service;

    @PostMapping("/save")
    public ApiResponse<Boolean> save(@RequestBody EvaluationTemplateDto dto) {
        return service.save(dto);
    }

    @GetMapping("/find-by-id/{id}")
    public ApiResponse<EvaluationTemplateDto> findById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("/paging")
    public ApiResponse<Page<EvaluationTemplateDto>> paging(@RequestBody SearchDto dto) {
        return service.paging(dto);
    }
}
