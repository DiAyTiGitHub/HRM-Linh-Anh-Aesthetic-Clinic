package com.globits.hr.service;

import com.globits.core.dto.SearchDto;
import com.globits.hr.dto.TransferTypeDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TransferTypeService {

    TransferTypeDto saveOrUpdate(TransferTypeDto dto);

    Page<TransferTypeDto> searchByPage(SearchDto searchDto);

    TransferTypeDto getById(UUID id);

    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    TransferTypeDto findByCode(String code);

    Boolean checkCode(TransferTypeDto dto);
}