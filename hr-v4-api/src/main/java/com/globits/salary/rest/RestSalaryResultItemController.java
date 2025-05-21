package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.salary.dto.search.ChooseSalaryResultItemDto;
import com.globits.salary.dto.SalaryResultItemDto;
import com.globits.salary.service.SalaryResultItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-result-item")
public class RestSalaryResultItemController {
    @Autowired
    private SalaryResultItemService salaryResultItemService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/choose-result-items")
    public ResponseEntity<List<SalaryResultItemDto>> handleChooseResultItems(@RequestBody ChooseSalaryResultItemDto dto) {
        List<SalaryResultItemDto> response = salaryResultItemService.handleChooseResultItems(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
