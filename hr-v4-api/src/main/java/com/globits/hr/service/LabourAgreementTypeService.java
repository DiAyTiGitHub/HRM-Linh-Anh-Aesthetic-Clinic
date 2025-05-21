package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.LabourAgreementType;
import com.globits.hr.dto.LabourAgreementTypeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface LabourAgreementTypeService extends GenericService<LabourAgreementType, UUID> {
    Page<LabourAgreementTypeDto> searchByPage(SearchDto dto);

    LabourAgreementTypeDto saveOrUpdate(LabourAgreementTypeDto dto, UUID id);

    LabourAgreementTypeDto getOne(UUID id);

    void deleteOne(UUID id);

    Boolean checkCode(UUID id, String code);
}
