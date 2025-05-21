package com.globits.hr.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.domain.RankTitle;
import com.globits.hr.dto.LeavingJobReasonDto;
import com.globits.hr.repository.LeavingJobReasonRepository;
import com.globits.hr.service.LeavingJobReasonService;
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
public class LeavingJobReasonServiceImpl extends GenericServiceImpl<LeavingJobReason, UUID> implements LeavingJobReasonService {

    @Resource
    private LeavingJobReasonRepository leavingJobReasonRepository;

    @Override
    @Modifying
    @Transactional
    public LeavingJobReasonDto saveOrUpdate(LeavingJobReasonDto dto) {
        if (dto == null) {
            return null;
        }

        LeavingJobReason leavingJobReason = new LeavingJobReason();
        if (dto.getId() != null) leavingJobReason = leavingJobReasonRepository.findById(dto.getId()).orElse(null);
        if (leavingJobReason == null) leavingJobReason = new LeavingJobReason();

        leavingJobReason.setName(dto.getName());
        leavingJobReason.setCode(dto.getCode());
        leavingJobReason.setDescription(dto.getDescription());

        leavingJobReason = leavingJobReasonRepository.save(leavingJobReason);

        return new LeavingJobReasonDto(leavingJobReason);
    }

    @Override
    public Page<LeavingJobReasonDto> searchByPage(SearchDto searchDto) {
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

        String sqlCount = "select count(distinct entity.id) from LeavingJobReason as entity ";
        String sql = "select distinct new com.globits.hr.dto.LeavingJobReasonDto(entity) from LeavingJobReason as entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, LeavingJobReasonDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }

        List<LeavingJobReasonDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<LeavingJobReasonDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public LeavingJobReasonDto getById(UUID id) {
        LeavingJobReason optionalLeavingJobReason = leavingJobReasonRepository.findById(id).orElse(null);
        if (optionalLeavingJobReason != null) {
            return new LeavingJobReasonDto(optionalLeavingJobReason);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean remove(UUID id) {
        if (id == null) return false;

        LeavingJobReason leavingJobReason = leavingJobReasonRepository.findById(id).orElse(null);
        if (leavingJobReason == null) return false;

        leavingJobReasonRepository.delete(leavingJobReason);
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
    public LeavingJobReasonDto findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        List<LeavingJobReason> entities = leavingJobReasonRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new LeavingJobReasonDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean checkCode(LeavingJobReasonDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<LeavingJobReason> entities = leavingJobReasonRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<LeavingJobReason> entities = leavingJobReasonRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (LeavingJobReason entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

}
