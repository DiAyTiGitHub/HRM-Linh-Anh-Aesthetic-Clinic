package com.globits.hr.rest;

import com.globits.hr.dto.ProductAttributeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.ProductAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product-attribute")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestProductAttributeController {
    @Autowired
    private ProductAttributeService service;

    @GetMapping(value = "/get-all")
    public List<ProductAttributeDto> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}")
    public ProductAttributeDto getById(@PathVariable("id") UUID id) {
        return service.getProductAttribute(id);
    }

    @PostMapping(value = "/save")
    public ProductAttributeDto saveProductType(@RequestBody ProductAttributeDto dto) {
        return service.saveProductAttribute(dto);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean deleteProduct(@PathVariable("id") UUID id) {
        return service.deleteProductAttribute(id);
    }

    @PostMapping(value = "/paging")
    public Page<ProductAttributeDto> paging(@RequestBody SearchDto dto) {
        return service.paging(dto);
    }
}
