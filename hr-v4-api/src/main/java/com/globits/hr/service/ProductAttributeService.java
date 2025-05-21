package com.globits.hr.service;

import com.globits.hr.dto.ProductAttributeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductAttributeService {
    List<ProductAttributeDto> getAll();
    ProductAttributeDto getProductAttribute(UUID id);
    ProductAttributeDto saveProductAttribute(ProductAttributeDto dto);
    Boolean deleteProductAttribute(UUID id);
    Page<ProductAttributeDto> paging(SearchDto dto);
}
