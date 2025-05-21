package com.globits.hr.service;

import com.globits.core.domain.Country;
import com.globits.core.dto.CountryDto;
import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface HrCountryService extends GenericService<Country, UUID> {
    Page<CountryDto> searchByPage(SearchDto dto);

    CountryDto saveOne(CountryDto dto, UUID id);

    void remove(UUID id);

    Boolean checkCode(UUID id, String code);

    CountryDto findByCode(String code);

}
