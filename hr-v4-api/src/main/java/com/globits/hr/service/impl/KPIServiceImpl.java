package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.KPIDto;
import com.globits.hr.dto.KPIItemDto;
import com.globits.hr.dto.KPIResultDto;
import com.globits.hr.dto.ProductDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.KPIService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class KPIServiceImpl extends GenericServiceImpl<KPI, UUID> implements KPIService {
    @Autowired
    private KPIRepository kpiRepository;
    @Autowired
    private KPIItemRepository kpiItemRepository;
    @Override
    public KPIDto getKPIById(UUID id) {
        if (id != null) {
            KPI entity = kpiRepository.findById(id).orElse(null);
            if (entity != null) {
                return new KPIDto(entity);
            }
        }
        return null;
    }

    @Override
    public KPIDto saveOrUpdate(KPIDto dto) {
        if (dto != null) {
            KPI entity = null;
            if (dto.getId() != null) {
                entity = kpiRepository.findById(dto.getId()).orElse(null);
            }
            if (entity == null) {
                entity = new KPI();
            }
            entity.setName(dto.getName());
            entity.setCode(dto.getCode());
            entity.setDescription(dto.getDescription());
            if (entity.getKpiItems() == null) {
                entity.setKpiItems(new HashSet<>());
            }
            entity.getKpiItems().clear();
            if (dto.getKpiItems() != null && !dto.getKpiItems().isEmpty()) {
                for (KPIItemDto item : dto.getKpiItems()) {
                    KPIItem kpiItem = null;
                    if (item.getId() != null) {
                        kpiItem = kpiItemRepository.findById(item.getId()).orElse(null);
                    }
                    if (kpiItem == null) {
                        kpiItem = new KPIItem();
                    }
                    kpiItem.setName(item.getName());
                    kpiItem.setCode(item.getCode());
                    kpiItem.setDescription(item.getDescription());
                    kpiItem.setWeight(item.getWeight());
                    kpiItem.setUsedForSalary(item.getUsedForSalary());
                    kpiItem.setKpi(entity);
                    entity.getKpiItems().add(kpiItem);
                }
            }
            KPI response = kpiRepository.save(entity);
            return new KPIDto(response);
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            KPI entity = kpiRepository.findById(id).orElse(null);
            if (entity != null) {
                kpiRepository.delete(entity);
                return true;
            }
        }
        return false;
    }

    @Override
    public Page<KPIDto> paging(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from KPI as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.KPIDto(entity) from KPI as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, KPIDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<KPIDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(KPIDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<KPI> entities = kpiRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<KPI> entities = kpiRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (KPI entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }
}
