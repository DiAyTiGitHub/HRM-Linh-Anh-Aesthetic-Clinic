package com.globits.hr.service.impl;

import com.globits.core.domain.Ethnics;
import com.globits.core.dto.EthnicsDto;
import com.globits.core.repository.EthnicsRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.HrEthinicsService;

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
public class HrEthinicsServiceImpl extends GenericServiceImpl<Ethnics, UUID> implements HrEthinicsService {
    @Autowired
    private EthnicsRepository ethnicsRepository;

    @Override
    public Page<EthnicsDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from Ethnics as entity where (1=1)";
        String sql = "select new com.globits.core.dto.EthnicsDto(entity) from Ethnics as entity where (1=1)";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) OR UPPER(entity.code) LIKE UPPER(:text) )";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, EthnicsDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<EthnicsDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public EthnicsDto saveOne(EthnicsDto dto, UUID id) {
        if (dto != null) {
            Ethnics entity = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<Ethnics> optional = ethnicsRepository.findById(id);
                if (optional.isPresent()) {
                    entity = optional.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new Ethnics();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity = ethnicsRepository.save(entity);
            return new EthnicsDto(entity);
        }
        return null;
    }

    @Override
    public void remove(UUID id) {
        Ethnics entity = null;
        Optional<Ethnics> optional = ethnicsRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            ethnicsRepository.delete(entity);
        }
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Ethnics entity = ethnicsRepository.findByCode(code);
            if (entity != null) {
                return id == null || !entity.getId().equals(id);
            }
            return false;
        }
        return null;
    }

    @Override
    public EthnicsDto getItemById(UUID id) {
        if (id != null) {
            Optional<Ethnics> ethnics = ethnicsRepository.findById(id);
            return ethnics.map(EthnicsDto::new).orElse(null);
        }
        return null;
    }

    @Override
    public EthnicsDto findByCode(String code) {
        if (!StringUtils.hasText(code)) return null;

        List<Ethnics> entities = ethnicsRepository.findListByCode(code);
        if (entities == null || entities.isEmpty())
            return null;

        return new EthnicsDto(entities.get(0));
    }

}
