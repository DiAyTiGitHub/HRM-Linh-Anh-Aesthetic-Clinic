package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HrEducationType;
import com.globits.hr.dto.HrEducationTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.HrEducationTypeRepository;
import com.globits.hr.service.HrEducationTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class HrEducationTypeServiceImpl extends GenericServiceImpl<HrEducationType, UUID>
		implements HrEducationTypeService {
	@PersistenceContext
	EntityManager manager;

	@Autowired
	HrEducationTypeRepository hrEducationTypeRepository;

	@Override
	public HrEducationTypeDto saveOrUpdate(HrEducationTypeDto dto, UUID id) {
		if (dto != null) {
			HrEducationType entity = null;
			if (id != null) {
				if (dto.getId() != null && !dto.getId().equals(id)) {
					return null;
				}
				Optional<HrEducationType> optional = hrEducationTypeRepository.findById(id);
				if (optional.isPresent()) {
					entity = optional.get();
				}
				if (entity != null) {
					entity.setModifyDate(LocalDateTime.now());
				}
			}
			if (entity == null) {
				entity = new HrEducationType();
				entity.setCreateDate(LocalDateTime.now());
				entity.setModifyDate(LocalDateTime.now());
			}
			entity.setName(dto.getName());
			entity.setCode(dto.getCode());
			entity.setNameEng(dto.getNameEng());
			entity.setDescription(dto.getDescription());
			entity = hrEducationTypeRepository.save(entity);
			return new HrEducationTypeDto(entity);
		}
		return null;
	}

	@Override
	public void remove(UUID id) {
		HrEducationType entity = null;
		Optional<HrEducationType> optional = hrEducationTypeRepository.findById(id);
		if (optional.isPresent()) {
			entity = optional.get();
		}
		if (entity != null) {
			hrEducationTypeRepository.delete(entity);
		}
	}

	@Override
	public HrEducationTypeDto getHrEducationType(UUID id) {
		HrEducationType entity = null;
		Optional<HrEducationType> optional = hrEducationTypeRepository.findById(id);
		if (optional.isPresent()) {
			entity = optional.get();
		}
		if (entity != null) {
			return new HrEducationTypeDto(entity);
		}
		return null;
	}

	@Override
	public Page<HrEducationTypeDto> searchByPage(SearchDto dto) {
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
		String sqlCount = "select count(entity.id) from HrEducationType as entity where (1=1)";
		String sql = "select new com.globits.hr.dto.HrEducationTypeDto(entity) from HrEducationType as entity where (1=1) ";

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) ) OR ( UPPER(entity.code) LIKE UPPER(:text) )";
		}
		sql += whereClause + orderBy;
		sqlCount += whereClause;

		Query q = manager.createQuery(sql, HrEducationTypeDto.class);
		Query qCount = manager.createQuery(sqlCount);

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			q.setParameter("text", '%' + dto.getKeyword() + '%');
			qCount.setParameter("text", '%' + dto.getKeyword() + '%');
		}

		int startPosition = pageIndex * pageSize;
		q.setFirstResult(startPosition);
		q.setMaxResults(pageSize);
		List<HrEducationTypeDto> entities = q.getResultList();
		long count = (long) qCount.getSingleResult();

		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		return new PageImpl<>(entities, pageable, count);
	}

	@Override
	public Boolean checkCode(UUID id, String code) {
		List<HrEducationType> entities = hrEducationTypeRepository.findByCode(code);
		if (entities != null && entities.size() > 0 && entities.get(0) != null && entities.get(0).getId() != null) {
			if (id != null && StringUtils.hasText(id.toString())) {
				return !entities.get(0).getId().equals(id);
			}
			return true;
		}
		return false;
	}

	@Override
	public HrEducationTypeDto findByCode(String code) {
		List<HrEducationType> entities = hrEducationTypeRepository.findByCode(code);
		if (entities == null || entities.size() == 0)
			return null;
		return new HrEducationTypeDto(entities.get(0));
	}

}
