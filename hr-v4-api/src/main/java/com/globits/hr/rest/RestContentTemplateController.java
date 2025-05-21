package com.globits.hr.rest;
import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.template.dto.ContentTemplateDto;
import com.globits.template.dto.SearchTemplateDto;
import com.globits.template.service.ContentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/content-template")
public class RestContentTemplateController {
    @Autowired
    private ContentTemplateService contentTemplateService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<ContentTemplateDto> saveOrUpdate(@RequestBody ContentTemplateDto dto) {
        dto.setAppCode(HrConstants.APP_CODE);
    	ContentTemplateDto response = contentTemplateService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/search-by-page")
    public ResponseEntity<Page<ContentTemplateDto>> searchByPage(@RequestBody SearchTemplateDto searchDto) {
        Page<ContentTemplateDto> page = contentTemplateService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deleteContentTemplate(@PathVariable("id") UUID id) {
        Boolean res = contentTemplateService.deleteContentTemplate(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/{id}")
    public ResponseEntity<ContentTemplateDto> getById(@PathVariable("id") UUID id) {
    	ContentTemplateDto result = contentTemplateService.getById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
