package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.core.dto.SearchDto;
import com.globits.hr.HrConstants;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.service.SalaryPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary-period")
public class RestSalaryPeriodController {
	@Autowired
	private SalaryPeriodService salaryPeriodService;

	@Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
	@RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
	public ResponseEntity<SalaryPeriodDto> saveOrUpdate(@RequestBody SalaryPeriodDto dto) {
		// SalaryPeriod code is duplicated
		Boolean isValidCode = salaryPeriodService.isValidCode(dto);
		if (isValidCode == null || isValidCode.equals(false)) {
			return new ResponseEntity<SalaryPeriodDto>(dto, HttpStatus.CONFLICT);
		}

		SalaryPeriodDto response = salaryPeriodService.saveOrUpdate(dto);

		if (response == null)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
	@RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<SalaryPeriodDto>> searchByPage(@RequestBody SearchDto searchDto) {
		Page<SalaryPeriodDto> page = salaryPeriodService.searchByPage(searchDto);
		if (page == null)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
	@RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
	public ResponseEntity<SalaryPeriodDto> getById(@PathVariable("id") UUID id) {
		SalaryPeriodDto result = salaryPeriodService.getById(id);
		if (result == null)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
	@DeleteMapping(value = "/remove/{id}")
	public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
		Boolean res = salaryPeriodService.remove(id);
		if (res == null)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
	@RequestMapping(value = "/remove-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> removeMultiple(@RequestBody List<UUID> ids) {
		Boolean deleted = salaryPeriodService.removeMultiple(ids);
		if (deleted) {
			return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/auto-gen-code/{configKey}")
	public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
		return new ResponseEntity<>(salaryPeriodService.autoGenerateCode(configKey), HttpStatus.OK);
	}
}
