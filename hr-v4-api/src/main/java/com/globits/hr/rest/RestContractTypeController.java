package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ContractTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.ContractTypeService;
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
@RequestMapping("/api/contractType")
@CrossOrigin(value = "*")
public class RestContractTypeController {
    @Autowired
    ContractTypeService contractTypeService;

    @PostMapping(value = "/searchByPage")
    public ResponseEntity<Page<ContractTypeDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<ContractTypeDto> result = contractTypeService.searchByPage(dto);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "")
    public ResponseEntity<ContractTypeDto> create(@RequestBody ContractTypeDto dto) {
        Boolean checkCode = contractTypeService.isValidCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        ContractTypeDto result = contractTypeService.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ContractTypeDto> update(@RequestBody ContractTypeDto dto, @PathVariable("id") UUID id) {
        UUID contractId = dto.getId() != null ? dto.getId() : id;
        Boolean checkCode = contractTypeService.isValidCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        ContractTypeDto result = contractTypeService.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ContractTypeDto> getOne(@PathVariable("id") UUID id) {
        ContractTypeDto result = contractTypeService.getOne(id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deleteOne(@PathVariable("id") UUID id) {
        contractTypeService.deleteOne(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = contractTypeService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/remove-multiple", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> removeMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = contractTypeService.removeMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get-all")
    public List<ContractTypeDto> getAllContractType() {
        return contractTypeService.getAllContractType();
    }
}
