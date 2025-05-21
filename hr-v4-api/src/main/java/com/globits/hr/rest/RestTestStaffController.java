package com.globits.hr.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.service.StaffService;

@RestController
@RequestMapping("/public/staff")
public class RestTestStaffController {
    @Autowired
    private StaffService staffService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.GET)
    public Page<StaffDto> getSimpleSearch(String textSearch) {
        int pageIndex = 1, pageSize = 10;
        return staffService.findPageByCode(textSearch, pageIndex, pageSize);
    }
}
