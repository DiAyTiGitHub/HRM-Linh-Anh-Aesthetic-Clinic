package com.globits.hr.rest;

import com.globits.hr.dto.HrDocumentTemplateDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.HrDocumentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hr-document-template")
public class RestHrDocumentTemplateController {

    @Autowired
    private HrDocumentTemplateService hrDocumentTemplateService;

    @RequestMapping(method = RequestMethod.POST, path = "/save")
    public ResponseEntity<HrDocumentTemplateDto> saveOrUpdate(@RequestBody HrDocumentTemplateDto dto) {
        Boolean isValidCode = hrDocumentTemplateService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<HrDocumentTemplateDto>(dto, HttpStatus.CONFLICT);
        }
        HrDocumentTemplateDto response = hrDocumentTemplateService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/paging", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<HrDocumentTemplateDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<HrDocumentTemplateDto> page = hrDocumentTemplateService.paging(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<HrDocumentTemplateDto> getById(@PathVariable("id") UUID id) {
        HrDocumentTemplateDto response = hrDocumentTemplateService.getHrDocumentTemplateById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean response = hrDocumentTemplateService.deleteById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
