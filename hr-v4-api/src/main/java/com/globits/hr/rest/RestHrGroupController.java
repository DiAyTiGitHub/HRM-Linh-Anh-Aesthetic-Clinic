package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrGroupDto;
import com.globits.hr.dto.OrganizationBranchDto;
import com.globits.hr.dto.search.SearchHrGroupDto;
import com.globits.hr.dto.search.SearchOrganizationBranchDto;
import com.globits.hr.service.HrGroupService;
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
@RequestMapping("/api/hr-groups")
public class RestHrGroupController {

    @Autowired
    private HrGroupService hrGroupService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping("/{hrGroupId}")
    public ResponseEntity<HrGroupDto> getHrGroupById(@PathVariable("hrGroupId") UUID hrGroupId) {
        HrGroupDto hrGroupDto = hrGroupService.getById(hrGroupId);
        if (hrGroupDto != null) {
            return ResponseEntity.ok(hrGroupDto);
        }
        return ResponseEntity.notFound().build();
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{hrGroupId}")
    public ResponseEntity<Boolean> deleteHrGroup(@PathVariable("hrGroupId") UUID hrGroupId) {
        Boolean res = hrGroupService.deleteHrGroup(hrGroupId);
        if (res == null || !res) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/saveHrGroup")
    public ResponseEntity<HrGroupDto> saveHrGroup(@RequestBody HrGroupDto dto) {
        //  System.out.println("Received request to create HrGroup: " + dto);
        HrGroupDto response = hrGroupService.saveHrGroup(dto);
        if(response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingHrGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<HrGroupDto>> pagingOrganizationBranch(@RequestBody SearchHrGroupDto searchDto) {
        Page<HrGroupDto> page = hrGroupService.pagingHrGroup(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // Xóa nhiều hrGroup
    @RequestMapping(value = "/deleteMultiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> hrGroupIds) {
//        System.out.println("Received IDs: " + hrGroupIds);
        Boolean deleted = hrGroupService.deleteMultipleHrGroup(hrGroupIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }


}
