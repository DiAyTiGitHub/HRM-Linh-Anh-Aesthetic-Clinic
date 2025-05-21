/*
 * TA va Giang làm
 */

package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.ProfessionalDegree;
import com.globits.hr.dto.ProfessionalDegreeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ProfessionalDegreeService extends GenericService<ProfessionalDegree, UUID> {
    ProfessionalDegreeDto saveProfessionalDegree(ProfessionalDegreeDto dto);

    Boolean deleteProfessionalDegree(UUID id);

    ProfessionalDegreeDto getProfessionalDegree(UUID id);

    ProfessionalDegreeDto updateProfessionalDegree(ProfessionalDegreeDto dto);

    Page<ProfessionalDegreeDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);
}
