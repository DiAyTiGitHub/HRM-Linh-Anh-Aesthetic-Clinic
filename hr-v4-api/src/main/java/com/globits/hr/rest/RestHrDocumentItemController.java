package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrDocumentItemDto;
import com.globits.hr.dto.search.SearchHrDocumentItemDto;
import com.globits.hr.service.HrDocumentItemService;
import com.globits.salary.dto.SalaryItemDto;
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
@RequestMapping("/api/hr-document-item")
public class RestHrDocumentItemController {
    @Autowired
    private HrDocumentItemService hrDocumentItemService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/save-or-update")
    public ResponseEntity<HrDocumentItemDto> saveHrDocumentItem(@RequestBody HrDocumentItemDto dto) {
        // HrDocumentItem's code is duplicated
        Boolean isValidCode = hrDocumentItemService.isValidCode(dto);
        if (!isValidCode) {
            return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
        }

        HrDocumentItemDto response = hrDocumentItemService.saveHrDocumentItem(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/save-list")
    public ResponseEntity<?> saveListHrDocumentItem(@RequestBody List<HrDocumentItemDto> dtos) {
        Integer response = hrDocumentItemService.saveListHrDocumentItems(dtos);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/paging")
    public ResponseEntity<Page<HrDocumentItemDto>> pagingHrDocumentItem(@RequestBody SearchHrDocumentItemDto searchDto) {
        Page<HrDocumentItemDto> page = hrDocumentItemService.pagingHrDocumentItem(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deleteHrDocumentItem(@PathVariable("id") UUID id) {
        Boolean res = hrDocumentItemService.deleteHrDocumentItem(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/{id}")
    public ResponseEntity<HrDocumentItemDto> getById(@PathVariable("id") UUID id) {
        HrDocumentItemDto result = hrDocumentItemService.getById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/delete-multiple")
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = hrDocumentItemService.deleteMultipleHrDocumentItems(ids);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}
