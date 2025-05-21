package com.globits.hr.rest;


import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationCandidateRoundDto;
import com.globits.hr.service.EvaluationCandidateRoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/evaluation-candidate-round")
@CrossOrigin(value = "*")
public class RestEvaluationCandidateRoundController {

    @Autowired
    private EvaluationCandidateRoundService evaluationCandidateRoundService;

    @GetMapping(value = "/get-by-round/{id}")
    public ApiResponse<EvaluationCandidateRoundDto> getByCandidateRoundId(@PathVariable("id") UUID candidateRoundId) {
        return evaluationCandidateRoundService.getByCandidateRoundId(candidateRoundId);
    }

    @PostMapping(value = "/save")
    public ApiResponse<EvaluationCandidateRoundDto> saveAndUpdate(@RequestBody EvaluationCandidateRoundDto dto) {
        return evaluationCandidateRoundService.saveAndUpdate(dto);
    }
}
