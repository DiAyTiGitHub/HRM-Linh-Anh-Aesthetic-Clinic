package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.InformaticDegree;
import com.globits.hr.dto.InformaticDegreeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface InformaticDegreeService extends GenericService<InformaticDegree, UUID> {
    InformaticDegreeDto saveInformaticDegree(InformaticDegreeDto dto);

    Boolean deleteInformaticDegree(UUID id);

    InformaticDegreeDto getInformaticDegree(UUID id);

    InformaticDegreeDto updateInformaticDegree(InformaticDegreeDto dto, UUID id);

    Page<InformaticDegreeDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);
}
