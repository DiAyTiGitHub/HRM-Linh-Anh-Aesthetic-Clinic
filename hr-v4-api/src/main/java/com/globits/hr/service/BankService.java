package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Bank;
import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface BankService extends GenericService<Bank, UUID> {
    BankDto getById(UUID id);

    Boolean checkCode(BankDto dto);

    BankDto saveOrUpdate(BankDto dto);

    Boolean deleteById(UUID id);

    Integer deleteMultiple(List<UUID> ids);

    Page<BankDto> paging(SearchDto dto);
}
