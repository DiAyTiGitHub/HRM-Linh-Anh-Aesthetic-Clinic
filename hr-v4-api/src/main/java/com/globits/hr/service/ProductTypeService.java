package com.globits.hr.service;

import com.globits.hr.dto.ProductTypeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductTypeService {
    List<ProductTypeDto> getAll();
    ProductTypeDto getProductType(UUID id);
    ProductTypeDto saveProductType(ProductTypeDto dto);
    Boolean deleteProductType(UUID id);
    Page<ProductTypeDto> paging(SearchDto dto);
    Boolean checkCode(ProductTypeDto dto);

    String autoGenerateCode(String configKey);
}
