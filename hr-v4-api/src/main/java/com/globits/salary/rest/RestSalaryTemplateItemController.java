package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.salary.dto.RequestSalaryValueDto;
import com.globits.salary.dto.SalaryResultItemDto;
import com.globits.salary.dto.SalaryTemplateItemDto;
import com.globits.salary.dto.search.ChooseSalaryResultItemDto;
import com.globits.salary.dto.search.ChooseSalaryTemplateItemDto;
import com.globits.salary.service.SalaryResultItemService;
import com.globits.salary.service.SalaryTemplateItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/salary-template-item")
public class RestSalaryTemplateItemController {
    @Autowired
    private SalaryTemplateItemService salaryTemplateItemService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/choose-template-items")
    public ResponseEntity<List<SalaryTemplateItemDto>> handleChooseTemplateItems(@RequestBody ChooseSalaryTemplateItemDto dto) {
        List<SalaryTemplateItemDto> response = salaryTemplateItemService.handleChooseTemplateItems(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/list")
    public ResponseEntity<List<SalaryTemplateItemDto>> getList(@RequestBody RequestSalaryValueDto dto) {
        List<SalaryTemplateItemDto> response = salaryTemplateItemService.getListSalaryTemplateItem(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-with-item-config")
    public ResponseEntity<SalaryTemplateItemDto> saveSalaryTemplateItemWithSalaryTemplateItemConfig(@RequestBody SalaryTemplateItemDto dto) {
        SalaryTemplateItemDto response = salaryTemplateItemService.saveSalaryTemplateItemWithSalaryTemplateItemConfig(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
