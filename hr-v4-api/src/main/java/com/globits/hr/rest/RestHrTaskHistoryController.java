package com.globits.hr.rest;

import com.globits.hr.dto.HrTaskHistoryDto;
import com.globits.hr.dto.search.SearchTaskHistoryDto;
import com.globits.hr.service.HrTaskHistoryService;
import com.globits.task.dto.HrTaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task-history")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrTaskHistoryController {
    @Autowired
    private HrTaskHistoryService hrTaskHistoryService;

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.POST)
    public ResponseEntity<List<HrTaskHistoryDto>> pagingHistoryOfTask(@PathVariable UUID taskId, @RequestBody SearchTaskHistoryDto searchObject) {
        List<HrTaskHistoryDto> res = hrTaskHistoryService.pagingHistoryOfTask(taskId, searchObject);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/task/all/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<List<HrTaskHistoryDto>> getAllHistoryOfTask(@PathVariable UUID taskId) {
        List<HrTaskHistoryDto> res = hrTaskHistoryService.getAllHistoryOfTask(taskId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public ResponseEntity<HrTaskHistoryDto> createHistoryComment(@RequestBody HrTaskDto taskComment){
        HrTaskHistoryDto res = hrTaskHistoryService.createHistoryComment(taskComment);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
