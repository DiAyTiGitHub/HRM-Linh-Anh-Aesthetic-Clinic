package com.globits.hr.rest;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.RankTitleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.RewardFormDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.RewardFormService;

@RestController
@RequestMapping("/api/reward-form")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestRewardFormController {
    @Autowired
    private RewardFormService rewardFormService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<RewardFormDto> saveRewardForm(@RequestBody RewardFormDto dto) {
        Boolean checkCode = rewardFormService.checkCode(dto.getId(), dto.getCode());
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        RewardFormDto response = rewardFormService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<RewardFormDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<RewardFormDto> page = this.rewardFormService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "")
    public ResponseEntity<Boolean> delete(@RequestBody List<UUID> ids) {
        rewardFormService.delete(ids);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deleteOne(@PathVariable("id") UUID id) {
        rewardFormService.deleteOne(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/check-code", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = rewardFormService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<RewardFormDto> getOne(@PathVariable UUID id) {
        RewardFormDto result = rewardFormService.getOne(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
