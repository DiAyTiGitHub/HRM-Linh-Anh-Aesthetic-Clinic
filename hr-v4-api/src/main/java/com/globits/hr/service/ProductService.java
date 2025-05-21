package com.globits.hr.service;

import com.globits.hr.dto.ProductDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductDto> getAll();
    ProductDto getProduct(UUID id);
    ProductDto saveProduct(ProductDto dto);
    Boolean deleteProduct(UUID id);
    Page<ProductDto> paging(SearchDto dto);

    Boolean checkCode(ProductDto dto);

    String autoGenerateCode(String configKey);
}
