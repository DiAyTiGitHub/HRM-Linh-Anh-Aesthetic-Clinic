package com.globits.hr.rest;


import com.globits.budget.dto.budget.BudgetDto;
import com.globits.hr.dto.ProductDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestProductController {
    @Autowired
    private ProductService service;

    @GetMapping(value = "/get-all")
    public List<ProductDto> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}")
    public ProductDto getById(@PathVariable("id") UUID id) {
        return service.getProduct(id);
    }

    @PostMapping(value = "/save")
    public ResponseEntity<ProductDto> saveProductType(@RequestBody ProductDto dto) {
        Boolean checkCode = service.checkCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        ProductDto response = service.saveProduct(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean deleteProduct(@PathVariable("id") UUID id) {
        return service.deleteProduct(id);
    }

    @PostMapping(value = "/paging")
    public Page<ProductDto> paging(@RequestBody SearchDto dto) {
        return service.paging(dto);
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(service.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
