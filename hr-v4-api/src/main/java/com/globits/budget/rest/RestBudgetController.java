package com.globits.budget.rest;

import com.globits.budget.dto.budget.*;
import com.globits.budget.service.BudgetService;
import com.globits.hr.dto.LeavingJobReasonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budget")
public class RestBudgetController {
    @Autowired
    private BudgetService service;

    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    public ResponseEntity<Page<BudgetDto>> pagingBudget(@RequestBody BudgetSearchDto dto) {
        Page<BudgetDto> result = service.pagingBudget(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean deleteById(@PathVariable UUID id) {
        return service.deleteById(id);
    }

    @RequestMapping(value = "/deleteMultiple", method = RequestMethod.POST)
    public ResponseEntity<?> deleteMultiple(@RequestBody List<UUID> ids) {
        Integer result = service.deleteMultiple(ids);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<BudgetDto> getBudgetById(@PathVariable UUID id) {
        BudgetDto budgetDto = service.getById(id);
        if (budgetDto == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(budgetDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<BudgetDto> saveBudget(@RequestBody BudgetDto dto) {
        Boolean checkCode = service.checkCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        BudgetDto response = service.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/summary", method = RequestMethod.POST)
    public ResponseEntity<List<BudgetSummaryDto>> getBudgetSummary(@RequestBody BudgetSummaryDto dto) {
        List<BudgetSummaryDto> result = service.getBudgetSummary(dto);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/summary-year", method = RequestMethod.POST)
    public ResponseEntity<List<BudgetSummaryYearDto>> getBudgetSummaryYear(@RequestBody BudgetSummaryDto dto) {
        List<BudgetSummaryYearDto> result = service.getBudgetSummaryYear(dto);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @RequestMapping(value = "/summary-balance", method = RequestMethod.POST)
    public ResponseEntity<BudgetSummaryBalanceDto> getBudgetSummaryBalance(@RequestBody BudgetSummaryDto dto) {
        BudgetSummaryBalanceDto result = service.getBudgetSummaryBalance(dto);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
