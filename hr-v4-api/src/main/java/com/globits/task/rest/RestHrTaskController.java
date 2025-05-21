package com.globits.task.rest;

import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchTaskDto;
import com.globits.hr.service.UserExtService;
import com.globits.task.dto.HrTaskDto;
import com.globits.task.dto.KanbanDto;
import com.globits.task.service.HrTaskService;
import com.globits.timesheet.dto.TimeSheetDetailDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hr-task")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrTaskController {
    @Autowired
    private HrTaskService hrTaskService;

    @Autowired
    private UserExtService userExtService;

    @RequestMapping(method = RequestMethod.POST)
    public HrTaskDto save(@RequestBody HrTaskDto hrTaskDto) {
        return hrTaskService.createOrUpdate(hrTaskDto, null);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public HrTaskDto edit(@RequestBody HrTaskDto hrTaskDto, @PathVariable UUID id) {
        return hrTaskService.createOrUpdate(hrTaskDto, id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<HrTaskDto> getById(@PathVariable UUID id) {
        HrTaskDto result = hrTaskService.getById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        hrTaskService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public ResponseEntity<?> search(@RequestBody SearchDto dto) {
        List<HrTaskDto> list = hrTaskService.searchByList(dto);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // @RequestMapping(value = "/update-status", method = RequestMethod.PUT)
    // public ResponseEntity<String> updateStatus(@RequestBody SearchDto dto) {
    // String result = hrTaskService.updateStatus(dto);
    // return new ResponseEntity<>(result, HttpStatus.OK);
    // }

    @RequestMapping(value = "/update-status", method = RequestMethod.PUT)
    public ResponseEntity<?> updateStatusNew(@RequestBody SearchDto dto) {
        KanbanDto result = hrTaskService.updateStatusNew(dto);
        return new ResponseEntity<KanbanDto>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/paging-list-task", method = RequestMethod.POST)
    public ResponseEntity<?> pagingListTask(@RequestBody SearchTaskDto dto) {
        Page<KanbanDto> page = hrTaskService.pagingListTask(dto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(path = "/get-list-by-limit", method = RequestMethod.POST)
    public ResponseEntity<?> getListByLimit(@RequestBody SearchTaskDto dto) {
        List<KanbanDto> list = hrTaskService.getListByLimit(dto);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-upcoming-code/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUpcomingTaskCodeInProject(@PathVariable("projectId") UUID projectId) {
        String result = hrTaskService.getUpcomingTaskCodeInProject(projectId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-by-project-activity/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<KanbanDto>> getByProjectActivity(@PathVariable UUID id) {
        List<KanbanDto> list = hrTaskService.getByProjectActivity(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
