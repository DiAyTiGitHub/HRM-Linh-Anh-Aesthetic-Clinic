package com.globits.hr.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.DeferredType;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.dto.DeferredTypeDto;
import com.globits.hr.repository.DeferredTypeRepository;
import com.globits.hr.service.DeferredTypeService;
import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeferredTypeServiceImpl extends GenericServiceImpl<DeferredType, UUID> implements DeferredTypeService {

    @Resource
    private DeferredTypeRepository deferredTypeRepository;

    @Override
    @Modifying
    @Transactional
    public DeferredTypeDto saveOrUpdate(DeferredTypeDto dto) {
        if (dto == null) {
            return null;
        }

        DeferredType deferredType = new DeferredType();
        if (dto.getId() != null) deferredType = deferredTypeRepository.findById(dto.getId()).orElse(null);
        if (deferredType == null) deferredType = new DeferredType();

        deferredType.setName(dto.getName());
        deferredType.setCode(dto.getCode());
        deferredType.setDescription(dto.getDescription());

        deferredType = deferredTypeRepository.save(deferredType);

        return new DeferredTypeDto(deferredType);
    }

    @Override
    public Page<DeferredTypeDto> searchByPage(SearchDto searchDto) {
        if (searchDto == null) {
            return null;
        }

        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(distinct entity.id) from DeferredType as entity ";
        String sql = "select distinct new com.globits.hr.dto.DeferredTypeDto(entity) from DeferredType as entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, DeferredTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }

        List<DeferredTypeDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<DeferredTypeDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public DeferredTypeDto getById(UUID id) {
        DeferredType optionalDeferredType = deferredTypeRepository.findById(id).orElse(null);
        if (optionalDeferredType != null) {
            return new DeferredTypeDto(optionalDeferredType);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean remove(UUID id) {
        if (id == null) return false;

        DeferredType deferredType = deferredTypeRepository.findById(id).orElse(null);
        if (deferredType == null) return false;

        deferredTypeRepository.delete(deferredType);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean removeMultiple(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.remove(id);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public DeferredTypeDto findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        List<DeferredType> entities = deferredTypeRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new DeferredTypeDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean checkCode(DeferredTypeDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<DeferredType> entities = deferredTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<DeferredType> entities = deferredTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (DeferredType entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

}
