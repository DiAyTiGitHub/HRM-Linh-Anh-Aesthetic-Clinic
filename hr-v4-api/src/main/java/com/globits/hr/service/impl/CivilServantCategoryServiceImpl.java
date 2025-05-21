package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.CivilServantCategory;
import com.globits.hr.dto.CivilServantCategoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.CivilServantCategoryRepository;
import com.globits.hr.service.CivilServantCategoryService;

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
public class CivilServantCategoryServiceImpl extends GenericServiceImpl<CivilServantCategory, UUID>
        implements CivilServantCategoryService {
    @Autowired
    private CivilServantCategoryRepository civilServantCategoryRepository;

    @Override
    public CivilServantCategoryDto getCivilServantCategory(UUID id) {
        CivilServantCategory entity = null;
        Optional<CivilServantCategory> civil = civilServantCategoryRepository.findById(id);
        if (civil.isPresent()) {
            entity = civil.get();
        }
        if (entity == null) {
            return null;
        }
        return new CivilServantCategoryDto(entity);
    }

    @Override
    public Boolean removeCivilServantCategory(UUID id) {
        CivilServantCategory civilServantCategory = null;
        Optional<CivilServantCategory> civil = civilServantCategoryRepository.findById(id);
        if (civil.isPresent()) {
            civilServantCategory = civil.get();
        }
        if (civilServantCategory != null) {
            civilServantCategoryRepository.delete(civilServantCategory);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<CivilServantCategoryDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from CivilServantCategory as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.CivilServantCategoryDto(entity) from CivilServantCategory as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        if (dto.getIncludeVoided() != null && dto.getIncludeVoided().equals(true)) {
            whereClause += " AND (entity.voided = true) ";
        } else {
            whereClause += " AND (entity.voided is null or entity.voided = false) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, CivilServantCategoryDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<CivilServantCategoryDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = civilServantCategoryRepository.checkCode(id, code);
            return count != 0L;
        }
        return null;
    }

    @Override
    public Boolean checkName(UUID id, String name) {
        if (name != null) {
            Long count = civilServantCategoryRepository.checkName(id, name);
            return count != 0L;
        }
        return null;
    }

    @Override
    public CivilServantCategoryDto saveOrUpdate(UUID id, CivilServantCategoryDto dto) {
        if (dto != null && dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
            CivilServantCategory entity = null;
            if (dto.getId() != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<CivilServantCategory> civil = civilServantCategoryRepository.findById(dto.getId());
                if (civil.isPresent()) {
                    entity = civil.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new CivilServantCategory();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());

            entity = civilServantCategoryRepository.save(entity);
            return new CivilServantCategoryDto(entity);
        }
        return null;
    }
}
