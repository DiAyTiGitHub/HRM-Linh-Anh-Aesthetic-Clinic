package com.globits.hr.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.DisciplineReason;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.dto.DisciplineReasonDto;
import com.globits.hr.repository.DisciplineReasonRepository;
import com.globits.hr.service.DisciplineReasonService;
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
public class DisciplineReasonServiceImpl extends GenericServiceImpl<DisciplineReason, UUID> implements DisciplineReasonService {

    @Resource
    private DisciplineReasonRepository disciplineReasonRepository;

    @Override
    public DisciplineReasonDto getById(UUID id) {
        DisciplineReason optionalDisciplineReason = disciplineReasonRepository.findById(id).orElse(null);
        if (optionalDisciplineReason != null) {
            return new DisciplineReasonDto(optionalDisciplineReason);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public DisciplineReasonDto saveDisciplineReason(DisciplineReasonDto dto) {
        if (dto == null) {
            return null;
        }

        DisciplineReason disciplineReason = new DisciplineReason();
        if (dto.getId() != null) disciplineReason = disciplineReasonRepository.findById(dto.getId()).orElse(null);
        if (disciplineReason == null) disciplineReason = new DisciplineReason();

        disciplineReason.setName(dto.getName());
        disciplineReason.setCode(dto.getCode());
        disciplineReason.setDescription(dto.getDescription());

        disciplineReason = disciplineReasonRepository.save(disciplineReason);

        return new DisciplineReasonDto(disciplineReason);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteDisciplineReason(UUID id) {
        if (id == null) return false;

        DisciplineReason disciplineReason = disciplineReasonRepository.findById(id).orElse(null);
        if (disciplineReason == null) return false;

        disciplineReasonRepository.delete(disciplineReason);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultipleDisciplineReasons(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID disciplineReasonId : ids) {
            boolean deleteRes = this.deleteDisciplineReason(disciplineReasonId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<DisciplineReasonDto> pagingDisciplineReasons(SearchDto dto) {
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

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(distinct entity.id) from DisciplineReason as entity ";
        String sql = "select distinct new com.globits.hr.dto.DisciplineReasonDto(entity) from DisciplineReason as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, DisciplineReasonDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        List<DisciplineReasonDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<DisciplineReasonDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public DisciplineReasonDto findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        List<DisciplineReason> entities = disciplineReasonRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new DisciplineReasonDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean checkCode(DisciplineReasonDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<DisciplineReason> entities = disciplineReasonRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<DisciplineReason> entities = disciplineReasonRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (DisciplineReason entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

}
