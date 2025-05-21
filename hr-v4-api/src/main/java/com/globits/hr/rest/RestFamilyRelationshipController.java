package com.globits.hr.rest;

import java.io.ByteArrayInputStream;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.ImportFamilyRelationshipDto;
import com.globits.hr.utils.ImportExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.FamilyRelationshipDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.FamilyRelationshipService;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/familyrelationship")
public class RestFamilyRelationshipController {
    @Autowired
    private FamilyRelationshipService familyRelationshipService;

    @PostMapping(value = "/searchByPage")
    public ResponseEntity<Page<FamilyRelationshipDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<FamilyRelationshipDto> page = familyRelationshipService.searchByPage(dto);
        return new ResponseEntity<>(page, page != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<List<ImportFamilyRelationshipDto>> importOrganizationFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<ImportFamilyRelationshipDto> list = ImportExportExcelUtil.readFamilyRelationshipFile(bis);

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<FamilyRelationshipDto> getOne(@PathVariable("id") UUID id) {
        FamilyRelationshipDto result = familyRelationshipService.getOne(id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "")
    public ResponseEntity<FamilyRelationshipDto> create(@RequestBody FamilyRelationshipDto dto) {
        FamilyRelationshipDto result = familyRelationshipService.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<FamilyRelationshipDto> update(@RequestBody FamilyRelationshipDto dto, @PathVariable("id") UUID id) {
        FamilyRelationshipDto result = familyRelationshipService.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") UUID id) {
        try {
            familyRelationshipService.deleteOne(id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.CONFLICT);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id, @RequestParam("code") String code) {
        Boolean result = familyRelationshipService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
