package com.globits.timesheet.rest;

import com.globits.hr.dto.StaffDto;
import com.globits.hr.service.UserExtService;
import com.globits.timesheet.dto.JournalDto;
import com.globits.timesheet.dto.search.SearchJournalDto;
import com.globits.timesheet.service.JournalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/journal")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestJournalController {
    @Autowired
    JournalService journalService;

    @Autowired
    private UserExtService userExtService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody JournalDto dto) {
        StaffDto currentStaff = userExtService.getCurrentStaff();
        if (dto.getStaffId() == null && currentStaff != null) {
            dto.setStaffId(currentStaff.getId());
        }
        if (dto.getStaffId() == null) {
            return new ResponseEntity<>("Can not find staff", HttpStatus.OK);
        }
        JournalDto result = journalService.saveOrUpdate(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestBody JournalDto dto, @PathVariable UUID id) {
        StaffDto currentStaff = userExtService.getCurrentStaff();
        if (dto.getStaffId() == null && currentStaff != null) {
            dto.setStaffId(currentStaff.getId());
        }
        if (dto.getStaffId() == null) {
            return new ResponseEntity<>("Can not find staff", HttpStatus.OK);
        }
        JournalDto result = journalService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        Boolean result = journalService.deleteById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/paging-journals", method = RequestMethod.POST)
    public ResponseEntity<Page<JournalDto>> pagingJournals(@RequestBody SearchJournalDto dto) {
        Page<JournalDto> result = journalService.pagingJournals(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<JournalDto> getById(@PathVariable UUID id) {
        JournalDto result = journalService.getJournal(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/find-list-journal", method = RequestMethod.POST)
    public ResponseEntity<?> findListJournal(@RequestBody SearchJournalDto dto) {
        StaffDto currentStaff = userExtService.getCurrentStaff();
        if (dto.getStaffId() == null && currentStaff != null) {
            dto.setStaffId(currentStaff.getId());
        }
        if (dto.getStaffId() == null) {
            return new ResponseEntity<>("Can not find staff", HttpStatus.OK);
        }
        List<JournalDto> result = journalService.getListJournal(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
