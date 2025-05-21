package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HRDiscipline;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.HRDisciplineDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.HRDisciplineRepository;
import com.globits.hr.service.HRDisciplineService;

import com.globits.hr.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class HRDisciplineServiceImpl extends GenericServiceImpl<HRDiscipline, UUID> implements HRDisciplineService {
    @PersistenceContext
    EntityManager manager;

    @Autowired
    HRDisciplineRepository disciplineRepository;

    @Autowired
    SystemConfigService systemConfigService;

    @Override
    public Page<HRDisciplineDto> searchByPage(SearchDto dto) {
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
        String orderBy = " ORDER BY entity.id DESC";
        String sqlCount = "select count(entity.id) from HRDiscipline as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.HRDisciplineDto(entity) from HRDiscipline as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.code) LIKE UPPER(:text) ) OR ( UPPER(entity.name) LIKE UPPER(:text) )";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, HRDisciplineDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<HRDisciplineDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public HRDisciplineDto saveOrUpdate(HRDisciplineDto dto, UUID id) {
        if (dto == null) return null;

        HRDiscipline entity = null;

        if (id != null) {
            if (dto.getId() != null && !dto.getId().equals(id)) {
                return null;
            }
            Optional<HRDiscipline> optional = disciplineRepository.findById(id);
            if (optional.isPresent()) {
                entity = optional.get();
            }
            if (entity != null) {
                entity.setModifyDate(LocalDateTime.now());
            }
        }

        if (entity == null) {
            entity = new HRDiscipline();
            entity.setCreateDate(LocalDateTime.now());
            entity.setModifyDate(LocalDateTime.now());
        }

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setLanguageKey(dto.getLanguageKey());
        entity.setDisciplineType(dto.getDisciplineType());
        entity.setFormal(dto.getFormal());
        entity.setDescription(dto.getDescription());
        entity.setEvaluateYear(dto.getEvaluateYear());
        entity.setLevel(dto.getLevel());

        entity = disciplineRepository.save(entity);

        return new HRDisciplineDto(entity);
    }

    @Override
    public HRDisciplineDto getOne(UUID id) {
        HRDiscipline entity = null;
        Optional<HRDiscipline> optional = disciplineRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new HRDisciplineDto(entity);
        }
        return null;
    }

    @Override
    public void deleteOne(UUID id) {
        HRDiscipline entity = null;
        Optional<HRDiscipline> optional = disciplineRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            disciplineRepository.delete(entity);
        }
    }


    @Override
    public Boolean checkCode(UUID id, String code) {
        List<HRDiscipline> entities = disciplineRepository.findByCode(code);
        if (entities != null && entities.size() > 0 && entities.get(0) != null && entities.get(0).getId() != null) {
            if (id != null && StringUtils.hasText(id.toString())) {
                return !entities.get(0).getId().equals(id);
            }
            return true;
        }
        return false;
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = disciplineRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }
}
