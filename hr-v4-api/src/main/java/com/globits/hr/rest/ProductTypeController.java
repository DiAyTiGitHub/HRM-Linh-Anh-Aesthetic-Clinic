package com.globits.hr.rest;

import com.globits.budget.dto.budget.BudgetDto;
import com.globits.hr.dto.ProductTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-type")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductTypeController {
    @Autowired
    private ProductTypeService service;

    @GetMapping(value = "/get-all")
    public List<ProductTypeDto> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}")
    public ProductTypeDto getById(@PathVariable("id") UUID id) {
        return service.getProductType(id);
    }

    @PostMapping(value = "/save")
    public ResponseEntity<ProductTypeDto> saveProductType(@RequestBody ProductTypeDto dto) {
        Boolean checkCode = service.checkCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        ProductTypeDto response = service.saveProductType(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean deleteProductType(@PathVariable("id") UUID id) {
        return service.deleteProductType(id);
    }

    @PostMapping(value = "/paging")
    public Page<ProductTypeDto> paging(@RequestBody SearchDto dto) {
        return service.paging(dto);
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(service.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
