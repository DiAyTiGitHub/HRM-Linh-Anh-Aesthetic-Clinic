package com.globits.hr.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.RankTitle;
import com.globits.hr.domain.StaffType;
import com.globits.hr.dto.StaffTypeDto;
import com.globits.hr.repository.StaffTypeRepository;
import com.globits.hr.service.StaffTypeService;
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
public class StaffTypeServiceImpl extends GenericServiceImpl<StaffType, UUID> implements StaffTypeService {

    @Resource
    private StaffTypeRepository staffTypeRepository;

    @Override
    @Modifying
    @Transactional
    public StaffTypeDto saveOrUpdate(StaffTypeDto dto) {
        if (dto == null) {
            return null;
        }

        StaffType staffType = new StaffType();
        if (dto.getId() != null) staffType = staffTypeRepository.findById(dto.getId()).orElse(null);
        if (staffType == null) staffType = new StaffType();

        staffType.setName(dto.getName());
        staffType.setCode(dto.getCode());
        staffType.setDescription(dto.getDescription());

        staffType = staffTypeRepository.save(staffType);

        return new StaffTypeDto(staffType);
    }

    @Override
    public Page<StaffTypeDto> searchByPage(SearchDto searchDto) {
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
        String orderBy = " ORDER BY entity.modifyDate desc, entity.createDate desc ";

        String sqlCount = "select count(distinct entity.id) from StaffType as entity ";
        String sql = "select distinct new com.globits.hr.dto.StaffTypeDto(entity) from StaffType as entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, StaffTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }

        List<StaffTypeDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<StaffTypeDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public StaffTypeDto getById(UUID id) {
        StaffType optionalStaffType = staffTypeRepository.findById(id).orElse(null);
        if (optionalStaffType != null) {
            return new StaffTypeDto(optionalStaffType);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean remove(UUID id) {
        if (id == null) return false;

        StaffType staffType = staffTypeRepository.findById(id).orElse(null);
        if (staffType == null) return false;

        staffTypeRepository.delete(staffType);
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
    public StaffTypeDto findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        List<StaffType> entities = staffTypeRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new StaffTypeDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean checkCode(StaffTypeDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<StaffType> entities = staffTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<StaffType> entities = staffTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (StaffType entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

}
