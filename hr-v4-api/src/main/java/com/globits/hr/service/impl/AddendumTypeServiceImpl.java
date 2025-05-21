package com.globits.hr.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.AddendumType;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.dto.AddendumTypeDto;
import com.globits.hr.repository.AddendumTypeRepository;
import com.globits.hr.service.AddendumTypeService;
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
public class AddendumTypeServiceImpl extends GenericServiceImpl<AddendumType, UUID> implements AddendumTypeService {

    @Resource
    private AddendumTypeRepository addendumTypeRepository;

    @Override
    @Modifying
    @Transactional
    public AddendumTypeDto saveOrUpdate(AddendumTypeDto dto) {
        if (dto == null) {
            return null;
        }

        AddendumType addendumType = new AddendumType();
        if (dto.getId() != null) addendumType = addendumTypeRepository.findById(dto.getId()).orElse(null);
        if (addendumType == null) addendumType = new AddendumType();

        addendumType.setName(dto.getName());
        addendumType.setCode(dto.getCode());
        addendumType.setDescription(dto.getDescription());

        addendumType = addendumTypeRepository.save(addendumType);

        return new AddendumTypeDto(addendumType);
    }

    @Override
    public Page<AddendumTypeDto> searchByPage(SearchDto searchDto) {
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

        String sqlCount = "select count(distinct entity.id) from AddendumType as entity ";
        String sql = "select distinct new com.globits.hr.dto.AddendumTypeDto(entity) from AddendumType as entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, AddendumTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }

        List<AddendumTypeDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<AddendumTypeDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public AddendumTypeDto getById(UUID id) {
        AddendumType optionalAddendumType = addendumTypeRepository.findById(id).orElse(null);
        if (optionalAddendumType != null) {
            return new AddendumTypeDto(optionalAddendumType);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean remove(UUID id) {
        if (id == null) return false;

        AddendumType addendumType = addendumTypeRepository.findById(id).orElse(null);
        if (addendumType == null) return false;

        addendumTypeRepository.delete(addendumType);
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
    public AddendumTypeDto findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        List<AddendumType> entities = addendumTypeRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new AddendumTypeDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean checkCode(AddendumTypeDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<AddendumType> entities = addendumTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<AddendumType> entities = addendumTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (AddendumType entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

}
