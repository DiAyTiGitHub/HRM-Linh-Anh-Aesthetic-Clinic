package com.globits.hr.service.impl;

import com.globits.core.domain.Religion;
import com.globits.core.dto.ReligionDto;
import com.globits.core.repository.ReligionRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.select.ReligionSelectDto;
import com.globits.hr.service.HrReligionService;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class HrReligionServiceImpl extends GenericServiceImpl<Religion, UUID> implements HrReligionService {
    @Autowired
    private ReligionRepository religionRepository;

    @Override
    public Page<ReligionDto> searchByPage(SearchDto dto) {
        if (dto == null)
            return null;

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0)
            pageIndex--;
        else
            pageIndex = 0;

        String whereClause = "";
        String orderBy = " ORDER BY entity.code ASC";

        if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy()))
            orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC";

        String sqlCount = "select count(entity.id) from Religion as entity where (1=1)";
        String sql = "select new com.globits.core.dto.ReligionDto(entity) from Religion as entity where (1=1)";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword()))
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) OR UPPER(entity.code) LIKE UPPER(:text) )";

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, ReligionDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<ReligionDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public ReligionDto saveOne(ReligionDto dto, UUID id) {
        if (dto != null) {
            Religion entity = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<Religion> optional = religionRepository.findById(id);
                if (optional.isPresent()) {
                    entity = optional.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new Religion();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity = religionRepository.save(entity);
            return new ReligionDto(entity);
        }
        return null;
    }

    @Override
    public void remove(UUID id) {
        Religion entity = null;
        Optional<Religion> optional = religionRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            religionRepository.delete(entity);
        }
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Religion entity = religionRepository.findByCode(code);
            if (entity != null) {
                return id == null || !entity.getId().equals(id);
            }
            return false;
        }
        return null;
    }

    @Override
    public ReligionDto getById(UUID id) {
        if (id != null) {
            Optional<Religion> religion = religionRepository.findById(id);
            if (religion.isPresent()) {
                Religion entity = religion.get();
                return new ReligionDto(entity);
            }
            return null;
        }
        return null;
    }

    @Override
    public List<ReligionSelectDto> getAllReligions() {
        List<Religion> religionEntities = religionRepository.findAll();
        List<ReligionSelectDto> models = new ArrayList<>();
        for (Religion religionEntity : religionEntities) {
            models.add(new ReligionSelectDto(religionEntity));
        }
        return models;
    }

	@Override
	public ReligionDto findByCode(String code) {
		List<Religion> entities = religionRepository.findListByCode(code);
		if(entities == null || entities.size() == 0) return null;
		return new ReligionDto(entities.get(0));
	}
    
}
