package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.service.SalaryTemplateItemGroupService;
import com.globits.salary.service.SalaryTemplateService;
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
@RequestMapping("/api/salary-template-item-group")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestSalaryTemplateItemGroupController {
	@Autowired
	private SalaryTemplateItemGroupService templateItemGroupService;


}
