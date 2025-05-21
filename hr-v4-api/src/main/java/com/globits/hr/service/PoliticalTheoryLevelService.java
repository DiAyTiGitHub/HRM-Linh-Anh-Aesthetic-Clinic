package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.PoliticalTheoryLevel;
import com.globits.hr.dto.PoliticalTheoryLevelDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PoliticalTheoryLevelService extends GenericService<PoliticalTheoryLevel, UUID> {
    PoliticalTheoryLevelDto savePoliticalTheoryLevel(PoliticalTheoryLevelDto dto);

    Boolean deletePoliticalTheoryLevel(UUID id);

    PoliticalTheoryLevelDto getPoliticalTheoryLevel(UUID id);

    PoliticalTheoryLevelDto updatePoliticalTheoryLevel(PoliticalTheoryLevelDto dto, UUID id);

    Page<PoliticalTheoryLevelDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);
}
