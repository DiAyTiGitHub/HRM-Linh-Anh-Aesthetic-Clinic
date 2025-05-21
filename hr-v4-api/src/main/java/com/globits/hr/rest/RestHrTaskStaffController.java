package com.globits.hr.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.globits.task.dto.HrTaskStaffDto;
import com.globits.task.service.HrTaskStaffService;

import java.util.UUID;

@RestController
@RequestMapping("/api/hr-task-staff")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrTaskStaffController {
    @Autowired
    HrTaskStaffService hrTaskStaffService;

    @RequestMapping(method = RequestMethod.POST)
    public HrTaskStaffDto save(@RequestBody HrTaskStaffDto dto) {
        return hrTaskStaffService.createOrUpdate(dto, null);
    }
    @RequestMapping(path = "/save-by-uuid",method = RequestMethod.POST)
    public HrTaskStaffDto saveByUUID(@RequestParam UUID taskId, @RequestParam UUID staffId) {
        return hrTaskStaffService.createOrUpdateByUUID(taskId,staffId, null);
    }
    @RequestMapping(path = "/save-by-uuid/{id}",method = RequestMethod.PUT)
    public HrTaskStaffDto updateByUUID(@RequestParam UUID taskId, @RequestParam UUID staffId, @PathVariable UUID id) {
        return hrTaskStaffService.createOrUpdateByUUID(taskId,staffId, id);
    }
    @RequestMapping(path = "/{id}",method = RequestMethod.PUT)
    public HrTaskStaffDto update(@RequestBody HrTaskStaffDto dto, @PathVariable UUID id) {
        return hrTaskStaffService.createOrUpdate(dto, id);
    }
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        return new ResponseEntity<>(hrTaskStaffService.delete(id),HttpStatus.OK);
    }
}
