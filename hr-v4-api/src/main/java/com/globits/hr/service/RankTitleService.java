package com.globits.hr.service;

import com.globits.hr.dto.RankTitleDto;
import com.globits.hr.dto.search.SearchRankTitleDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RankTitleService {
    RankTitleDto saveRankTitle(RankTitleDto dto);

    Integer saveListRankTitle(List<RankTitleDto> dtos);

    RankTitleDto getById(UUID id);

    Boolean deleteRankTitle(UUID id);

    Boolean deleteMultipleRankTitles(List<UUID> ids);

    Page<RankTitleDto> pagingRankTitle(SearchRankTitleDto dto);

    RankTitleDto findByShortName(String shortName);

    Boolean checkShortName(RankTitleDto dto);

    String autoGenerateCode(String configKey);

}
