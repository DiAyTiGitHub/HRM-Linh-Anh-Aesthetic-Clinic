package com.globits.hr.rest;

import com.globits.hr.dto.InsurancePackageDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.InsurancePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/insurance-package")
public class RestInsurancePackageController {

    @Autowired
    private InsurancePackageService insurancePackageService;

    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<InsurancePackageDto> saveOrUpdate(@RequestBody InsurancePackageDto dto) {
        boolean isValidCode = insurancePackageService.isValidCode(dto);
        if (!isValidCode) {
            return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
        }
        InsurancePackageDto response = insurancePackageService.saveOrUpdate(dto);

        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<InsurancePackageDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<InsurancePackageDto> page = insurancePackageService.searchByPage(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<InsurancePackageDto> getById(@PathVariable("id") UUID id) {
        InsurancePackageDto response = insurancePackageService.getById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean response = insurancePackageService.deleteById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> deleteMultiple(@RequestBody List<UUID> ids) {
        Integer response = insurancePackageService.deleteMultiple(ids);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
