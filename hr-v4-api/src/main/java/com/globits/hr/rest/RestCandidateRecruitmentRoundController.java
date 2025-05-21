package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.CandidateRecruitmentRoundDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchCandidateDto;
import com.globits.hr.dto.search.SearchCandidateRecruitmentRoundDto;
import com.globits.hr.service.CandidateRecruitmentRoundService;
import com.globits.hr.service.CandidateService;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidate-recruitment-round")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestCandidateRecruitmentRoundController {
    @Autowired
    private CandidateRecruitmentRoundService candidateRecruitmentRoundService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-candidate-recruitment-round")
    public ResponseEntity<CandidateRecruitmentRoundDto> saveCandidateRecruitmentRound(@RequestBody CandidateRecruitmentRoundDto dto) {
        CandidateRecruitmentRoundDto response = candidateRecruitmentRoundService.saveCandidateRecruitmentRound(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/paging-candidate-recruitment-round", method = RequestMethod.POST)
    public ResponseEntity<Page<CandidateRecruitmentRoundDto>> pagingCandidateRecruitmentRound(@RequestBody SearchCandidateRecruitmentRoundDto searchDto) {
        Page<CandidateRecruitmentRoundDto> page = candidateRecruitmentRoundService.pagingCandidateRecruitmentRound(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean deleted = candidateRecruitmentRoundService.remove(id);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CandidateRecruitmentRoundDto> getById(@PathVariable("id") UUID id) {
        CandidateRecruitmentRoundDto result = candidateRecruitmentRoundService.getById(id);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-round-by-candidate/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<CandidateRecruitmentRoundDto>> getCandidateRoundByCandidateId(@PathVariable("id") UUID candidateId) {
        List<CandidateRecruitmentRoundDto> result = candidateRecruitmentRoundService.getCandidateRoundByCandidateId(candidateId);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> removeMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = candidateRecruitmentRoundService.removeMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    // update candidate's result in recruitment round
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-candidate-recruitment-round-result", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateCandidateRecruitmentRoundResult(@RequestBody SearchCandidateRecruitmentRoundDto dto) {
        try {
            Boolean isUpdated = candidateRecruitmentRoundService.updateCandidateRecruitmentRoundResult(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    // Phân bổ/Sắp xếp ứng viên cho các vòng tuyển dụng tiếp theo
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/move-to-next-recruitment-round", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> moveToNextRecruitmentRound(@RequestBody SearchCandidateRecruitmentRoundDto dto) {
        try {
            Boolean isUpdated = candidateRecruitmentRoundService.moveToNextRecruitmentRound(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    // Phân bổ/Sắp xếp ứng viên cho vòng tuyển dụng đầu tiên
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/distribute-candidates-for-first-recruitment-round", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> distributeCandidatesForFirstRecruitmentRound(@RequestBody SearchCandidateRecruitmentRoundDto dto) {
        try {
            Boolean isUpdated = candidateRecruitmentRoundService.distributeCandidatesForFirstRecruitmentRound(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/list-candidate/{planId}/{roundId}")
    public ApiResponse<List<CandidateRecruitmentRoundDto>> getListCandidate(@PathVariable UUID planId, @PathVariable UUID roundId) {
        return candidateRecruitmentRoundService.getListCandiDateByPlainAndRound(planId, roundId);
    }

    @GetMapping(value = "/get-by-round-id/{roundId}")
    public ApiResponse<List<CandidateRecruitmentRoundDto>> getByIdRecruitmentRound(@PathVariable UUID roundId) {
        return candidateRecruitmentRoundService.getByIdRecruitmentRound(roundId);
    }

    @GetMapping("/confirm-interview/{candidateId}/{decision}")
    public ApiResponse<Boolean> confirmInterview(@PathVariable UUID candidateId, @PathVariable HrConstants.CandidateRecruitmentRoundStatus decision) {
        return candidateRecruitmentRoundService.confirmInterview(candidateId, decision);
    }

    @GetMapping("/do-action-assignment/{crrId}/{status}")
    public ApiResponse<Boolean> doActionAssignment(@PathVariable UUID crrId, @PathVariable String status) {
        return candidateRecruitmentRoundService.doActionAssignment(crrId, HrConstants.CandidateStatus.valueOf(status));
    }

    @GetMapping("/pass-to-next-round/{crrId}")
    public ApiResponse<Boolean> passToNextRound(@PathVariable UUID crrId) {
        return candidateRecruitmentRoundService.passToNextRound(crrId);
    }

    @PostMapping("/pass-list-to-next-round")
    public ApiResponse<HashMap<UUID, HashMap<String, Object>>> passListToNextRound(@RequestBody List<UUID> crrIds) {
        return candidateRecruitmentRoundService.passListToNextRound(crrIds);
    }

    @GetMapping("/reject-candidate-round/{crrId}")
    public ApiResponse<Boolean> rejectCandidateRound(@PathVariable UUID crrId) {
        return candidateRecruitmentRoundService.rejectCandidateRound(crrId);
    }

    @GetMapping("/reject-list-candidate-round")
    public ApiResponse<Boolean> rejectListCandidateRound(@RequestBody List<UUID> crrIds) {
        return candidateRecruitmentRoundService.rejectListCandidateRound(crrIds);
    }
}
