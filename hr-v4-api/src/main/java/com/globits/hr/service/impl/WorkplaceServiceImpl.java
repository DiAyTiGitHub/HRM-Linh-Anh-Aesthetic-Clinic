package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Workplace;
import com.globits.hr.dto.WorkplaceDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.WorkplaceRepository;
import com.globits.hr.service.WorkplaceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.persistence.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkplaceServiceImpl extends GenericServiceImpl<Workplace, UUID> implements WorkplaceService {
    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Override
    public WorkplaceDto saveOrUpdate(WorkplaceDto dto) {
        if (dto == null) return null;

        Workplace entity = null;
        if (dto.getId() != null) {
            entity = workplaceRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new Workplace();
        }

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        
        entity = workplaceRepository.save(entity);

        return new WorkplaceDto(entity);
    }


    @Override
    public void deleteWorkplace(UUID id) {
        Workplace entity = null;
        Optional<Workplace> optional = workplaceRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            workplaceRepository.delete(entity);
        }
    }
    
	@Override
	@Transactional
	public Boolean deleteMultiple(List<UUID> ids) {
		if (ids == null)
			return false;
		for (UUID applicantId : ids) {
			this.deleteWorkplace(applicantId);
		}
		return true;
	}
	
	
    @Override
    public WorkplaceDto getWorkplaceById(UUID id) {
        Workplace entity = null;
        Optional<Workplace> optional = workplaceRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new WorkplaceDto(entity);
        }
        return null;
    }

    @Override
    public Page<WorkplaceDto> searchByPage(SearchDto dto) {
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
        String sqlCount = "select count(entity.id) from Workplace as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.WorkplaceDto(entity) from Workplace as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) ) OR ( UPPER(entity.code) LIKE UPPER(:text) )";
        }
        
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, WorkplaceDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
      
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<WorkplaceDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }
    
    @Override
    public Boolean isValidCode(WorkplaceDto dto) {
        if (dto == null)
            return false;
        List<Workplace> entities = workplaceRepository.findByCode(dto.getCode());
        if (entities == null || entities.isEmpty()) {
            return true;
        }
        if (dto.getId() == null) {
            return false;
        } else {
            for (Workplace entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }
}
