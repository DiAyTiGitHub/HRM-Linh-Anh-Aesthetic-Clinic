package com.globits.hr.rest;

import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank")
public class RestBankController {

    @Autowired
    private BankService bankService;

    @RequestMapping(method = RequestMethod.POST, path = "/save")
    public ResponseEntity<BankDto> saveOrUpdate(@RequestBody BankDto dto) {
        boolean isValidCode = bankService.checkCode(dto);
        if (!isValidCode) {
            return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
        }
        BankDto response = bankService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<BankDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<BankDto> page = bankService.paging(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<BankDto> getById(@PathVariable("id") UUID id) {
        BankDto response = bankService.getById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean response = bankService.deleteById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/deleteMultiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> deleteMultiple(@RequestBody List<UUID> ids) {
        Integer response = bankService.deleteMultiple(ids);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
