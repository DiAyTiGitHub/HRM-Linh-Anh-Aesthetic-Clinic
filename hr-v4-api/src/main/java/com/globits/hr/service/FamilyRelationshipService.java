package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.hr.dto.FamilyRelationshipDto;
import com.globits.hr.dto.search.SearchDto;

public interface FamilyRelationshipService {
    Page<FamilyRelationshipDto> searchByPage(SearchDto dto);

    FamilyRelationshipDto saveOrUpdate(FamilyRelationshipDto dto, UUID id);

    FamilyRelationshipDto getOne(UUID id);

    void deleteOne(UUID id);

    Boolean checkCode(UUID id, String code);
}
