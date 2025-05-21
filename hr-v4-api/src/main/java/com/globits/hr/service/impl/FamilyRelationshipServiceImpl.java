package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.FamilyRelationship;
import com.globits.hr.dto.FamilyRelationshipDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.FamilyRelationshipRepository;
import com.globits.hr.service.FamilyRelationshipService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class FamilyRelationshipServiceImpl extends GenericServiceImpl<FamilyRelationship, UUID> implements FamilyRelationshipService {
    @Autowired
    FamilyRelationshipRepository familyRelationshipRepository;

    @Override
    public Page<FamilyRelationshipDto> searchByPage(SearchDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = "";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(entity.id) from FamilyRelationship as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.FamilyRelationshipDto(entity) from FamilyRelationship as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, FamilyRelationshipDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<FamilyRelationshipDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public FamilyRelationshipDto saveOrUpdate(FamilyRelationshipDto dto, UUID id) {
        if (dto != null) {
            FamilyRelationship entity = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<FamilyRelationship> familyRelationship = familyRelationshipRepository.findById(id);
                if (familyRelationship.isPresent()) {
                    entity = familyRelationship.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new FamilyRelationship();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity = familyRelationshipRepository.save(entity);
            return new FamilyRelationshipDto(entity);
        }
        return null;
    }

    @Override
    public FamilyRelationshipDto getOne(UUID id) {
        FamilyRelationship familyRelationship = null;
        Optional<FamilyRelationship> optional = familyRelationshipRepository.findById(id);
        if (optional.isPresent()) {
            familyRelationship = optional.get();
        }
        if (familyRelationship != null) {
            return new FamilyRelationshipDto(familyRelationship);
        }
        return null;
    }

    @Override
    public void deleteOne(UUID id) {
        FamilyRelationship familyRelationship = null;
        Optional<FamilyRelationship> optional = familyRelationshipRepository.findById(id);
        if (optional.isPresent()) {
            familyRelationship = optional.get();
        }
        if (familyRelationship != null) {
            familyRelationshipRepository.delete(familyRelationship);
        }

    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = familyRelationshipRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }
}
