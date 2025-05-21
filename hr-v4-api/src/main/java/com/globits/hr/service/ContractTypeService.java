package com.globits.hr.service;

import com.globits.hr.dto.ContractTypeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ContractTypeService {
    Page<ContractTypeDto> searchByPage(SearchDto dto);

    ContractTypeDto saveOrUpdate(ContractTypeDto dto, UUID id);

    ContractTypeDto getOne(UUID id);

    void deleteOne(UUID id);

    Boolean checkCode(UUID id, String code);

    Boolean isValidCode(ContractTypeDto dto);

    Boolean removeMultiple(List<UUID> ids);

    List<ContractTypeDto> getAllContractType();
}
