package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.SalaryCalculationRequestDto;
import com.globits.salary.domain.SalaryResultStaffItem;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.SalaryResultStaffItemDto;
import com.globits.salary.dto.SalaryResultStaffPaySlipDto;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.excel.SalaryResultStaffItemImportDto;
import com.globits.salary.dto.search.SalaryCalculatePayslipDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.service.SalaryPayslipService;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.salary.service.SalaryResultStaffService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary-result-staff-item")
public class RestSalaryResultStaffItemController {
    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/import-salary-result-staff-item-value")
    public ResponseEntity<List<SalaryResultStaffItemDto>> importSalaryResultStaffItemValue(@RequestBody SalaryResultStaffItemImportDto dto) {
        List<SalaryResultStaffItemDto> response = salaryResultStaffItemService.importSalaryResultStaffItemValue(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
