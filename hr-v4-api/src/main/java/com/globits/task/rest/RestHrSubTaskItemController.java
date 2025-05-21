package com.globits.task.rest;

import com.globits.hr.service.UserExtService;
import com.globits.task.dto.HrSubTaskItemDto;
import com.globits.task.dto.SearchSubTaskDto;
import com.globits.task.service.HrSubTaskItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hr-sub-task-item")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrSubTaskItemController {
    @Autowired
    private HrSubTaskItemService hrSubTaskItemService;

    @Autowired
    private UserExtService userExtService;

    @RequestMapping(method = RequestMethod.POST)
    public HrSubTaskItemDto save(@RequestBody HrSubTaskItemDto hrSubTaskItemDto) {
        return hrSubTaskItemService.createOrUpdate(hrSubTaskItemDto, null);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public HrSubTaskItemDto edit(@RequestBody HrSubTaskItemDto hrSubTaskItemDto, @PathVariable UUID id) {
        return hrSubTaskItemService.createOrUpdate(hrSubTaskItemDto, id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<HrSubTaskItemDto> getById(@PathVariable UUID id) {
        HrSubTaskItemDto result = hrSubTaskItemService.getById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        return new ResponseEntity<>(hrSubTaskItemService.delete(id), HttpStatus.OK);
    }

    @RequestMapping(path = "/get-list-sub-task-item", method = RequestMethod.POST)
    public ResponseEntity<List<HrSubTaskItemDto>> getListSubTaskItem(@RequestBody SearchSubTaskDto dto) {
        List<HrSubTaskItemDto> result = hrSubTaskItemService.getListSubTaskItem(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
