package com.globits.hr.rest;

import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.BankService;
import com.globits.hr.service.HrRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hr-role-utils")
public class RestHrRoleController {

    @Autowired
    private HrRoleService hrRoleService;

    @RequestMapping(method = RequestMethod.GET, path = "/has-shift-assignment-permission")
    public ResponseEntity<Boolean> hasShiftAssignmentPermission() {
        boolean response = hrRoleService.hasShiftAssignmentPermission();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/has-shift-assignment-permission/{id}")
    public ResponseEntity<Boolean> hasShiftAssignmentPermission(@PathVariable UUID id) {
        boolean response = hrRoleService.hasShiftAssignmentPermission(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "/has-position-manager")
    public ResponseEntity<Boolean> hasPositionManager() {
        boolean response = hrRoleService.isHeadOfDepartment();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
