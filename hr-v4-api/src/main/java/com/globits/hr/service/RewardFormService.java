package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.RewardForm;
import com.globits.hr.dto.RewardFormDto;
import com.globits.hr.dto.search.SearchDto;

public interface RewardFormService extends GenericService<RewardForm, UUID> {
    RewardFormDto saveOrUpdate(RewardFormDto dto);

    RewardFormDto getOne(UUID id);

    Page<RewardFormDto> searchByPage(SearchDto dto);

    void deleteOne(UUID id);

    void delete(List<UUID> ids);

    Boolean checkCode(UUID id, String code);
}
