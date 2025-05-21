package com.globits.hr.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.domain.RefusalReason;
import com.globits.hr.dto.RefusalReasonDto;
import com.globits.hr.repository.RefusalReasonRepository;
import com.globits.hr.service.RefusalReasonService;
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
public class RefusalReasonServiceImpl extends GenericServiceImpl<RefusalReason, UUID> implements RefusalReasonService {

    @Resource
    private RefusalReasonRepository refusalReasonRepository;

    @Override
    @Modifying
    @Transactional
    public RefusalReasonDto saveOrUpdate(RefusalReasonDto dto) {
        if (dto == null) {
            return null;
        }

        RefusalReason refusalReason = new RefusalReason();
        if (dto.getId() != null) refusalReason = refusalReasonRepository.findById(dto.getId()).orElse(null);
        if (refusalReason == null) refusalReason = new RefusalReason();

        refusalReason.setName(dto.getName());
        refusalReason.setCode(dto.getCode());
        refusalReason.setDescription(dto.getDescription());

        refusalReason = refusalReasonRepository.save(refusalReason);

        return new RefusalReasonDto(refusalReason);
    }

    @Override
    public Page<RefusalReasonDto> searchByPage(SearchDto searchDto) {
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

        String sqlCount = "select count(distinct entity.id) from RefusalReason as entity ";
        String sql = "select distinct new com.globits.hr.dto.RefusalReasonDto(entity) from RefusalReason as entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, RefusalReasonDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }

        List<RefusalReasonDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<RefusalReasonDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public RefusalReasonDto getById(UUID id) {
        RefusalReason optionalRefusalReason = refusalReasonRepository.findById(id).orElse(null);
        if (optionalRefusalReason != null) {
            return new RefusalReasonDto(optionalRefusalReason);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean remove(UUID id) {
        if (id == null) return false;

        RefusalReason refusalReason = refusalReasonRepository.findById(id).orElse(null);
        if (refusalReason == null) return false;

        refusalReasonRepository.delete(refusalReason);
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
    public RefusalReasonDto findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        List<RefusalReason> entities = refusalReasonRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new RefusalReasonDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean checkCode(RefusalReasonDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<RefusalReason> entities = refusalReasonRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<RefusalReason> entities = refusalReasonRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (RefusalReason entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

}
