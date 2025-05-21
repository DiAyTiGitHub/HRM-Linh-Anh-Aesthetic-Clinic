package com.globits.budget.rest;

import com.globits.budget.dto.BudgetCategoryDto;
import com.globits.budget.dto.budget.BudgetSearchDto;
import com.globits.budget.service.BudgetCategoryService;
import com.globits.hr.dto.LeavingJobReasonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budget-category")
public class RestBudgetCategoryController {
    @Autowired
    private BudgetCategoryService service;

    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    public ResponseEntity<Page<BudgetCategoryDto>> pagingBudgetCategory(@RequestBody BudgetSearchDto dto) {
        Page<BudgetCategoryDto> result = service.pagingBudgetCategory(dto);
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
    public ResponseEntity<BudgetCategoryDto> getBudgetCategoryById(@PathVariable UUID id) {
        BudgetCategoryDto dto = service.getById(id);
        if (dto == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<BudgetCategoryDto> saveBudgetCategory(@RequestBody BudgetCategoryDto dto) {
        Boolean checkCode = service.checkCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        BudgetCategoryDto response = service.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
