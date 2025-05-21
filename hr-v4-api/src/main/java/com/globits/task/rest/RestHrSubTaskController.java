package com.globits.task.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globits.task.dto.HrSubTaskDto;
import com.globits.task.dto.SearchSubTaskDto;
import com.globits.task.service.HrSubTaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hr-sub-task")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrSubTaskController {
    @Autowired
    HrSubTaskService hrSubTaskService;

    @RequestMapping(method = RequestMethod.POST)
    public HrSubTaskDto save(@RequestBody HrSubTaskDto hrSubTaskDto) {
        return hrSubTaskService.createOrUpdate(hrSubTaskDto, null);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public HrSubTaskDto edit(@RequestBody HrSubTaskDto hrSubTaskDto, @PathVariable UUID id) {
        return hrSubTaskService.createOrUpdate(hrSubTaskDto, id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<HrSubTaskDto> getById(@PathVariable UUID id) {
        HrSubTaskDto result = hrSubTaskService.getById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        return new ResponseEntity<>(hrSubTaskService.delete(id), HttpStatus.OK);
    }

    @RequestMapping(path = "/get-list-sub-task", method = RequestMethod.POST)
    public ResponseEntity<List<HrSubTaskDto>> getListSubTask(@RequestBody SearchSubTaskDto dto) {
        List<HrSubTaskDto> result = hrSubTaskService.getListSubTask(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
