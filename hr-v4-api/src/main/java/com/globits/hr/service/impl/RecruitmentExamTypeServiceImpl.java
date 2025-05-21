package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.RecruitmentExamType;
import com.globits.hr.dto.RecruitmentExamTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.RecruitmentExamTypeRepository;
import com.globits.hr.service.RecruitmentExamTypeService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class RecruitmentExamTypeServiceImpl extends GenericServiceImpl<RecruitmentExamType, UUID> implements RecruitmentExamTypeService {

    @Autowired
    private RecruitmentExamTypeRepository recruitmentExamTypeRepository;

    @Override
    public Page<RecruitmentExamTypeDto> searchByPage(SearchDto dto) {
        if (dto == null)
            return null;

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = "";

        String orderBy = " ORDER BY entity.name DESC";
        if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy())) {
            orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC";
        }

        String sqlCount = "select count(entity.id) from RecruitmentExamType as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.RecruitmentExamTypeDto(entity) from RecruitmentExamType as entity where (1=1)";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) OR UPPER(entity.code) LIKE UPPER(:text) )";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, RecruitmentExamTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<RecruitmentExamTypeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public RecruitmentExamTypeDto saveOne(RecruitmentExamTypeDto dto, UUID id) {
        if (dto != null) {
            RecruitmentExamType entity = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<RecruitmentExamType> optional = recruitmentExamTypeRepository.findById(id);
                if (optional.isPresent()) {
                    entity = optional.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new RecruitmentExamType();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity = recruitmentExamTypeRepository.save(entity);
            return new RecruitmentExamTypeDto(entity);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteRecruitmentExamType(UUID id) {
        if (id == null) return false;

        RecruitmentExamType recruitmentExamType = recruitmentExamTypeRepository.findById(id).orElse(null);
        if (recruitmentExamType == null) return false;

        recruitmentExamTypeRepository.delete(recruitmentExamType);
        return true;
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (!StringUtils.hasText(code))
            return false;
        if (id == null) {
            List<RecruitmentExamType> entities = recruitmentExamTypeRepository.findByCode(code);
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<RecruitmentExamType> entities = recruitmentExamTypeRepository.findByCode(code);
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (RecruitmentExamType entity : entities) {
                if (!entity.getId().equals(id))
                    return false;
            }
        }
        return true;

    }

    @Override
    public RecruitmentExamTypeDto getItemById(UUID id) {
        if (id != null) {
            Optional<RecruitmentExamType> recruitmentExamType = recruitmentExamTypeRepository.findById(id);
            return recruitmentExamType.map(RecruitmentExamTypeDto::new).orElse(null);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID recruitmentExamTypeId : ids) {
            boolean deleteRes = this.deleteRecruitmentExamType(recruitmentExamTypeId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }
    
}
