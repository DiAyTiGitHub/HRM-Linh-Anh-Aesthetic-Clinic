package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.EducationalInstitution;
import com.globits.hr.dto.EducationalInstitutionDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.EducationalInstitutionRepository;
import com.globits.hr.service.EducationalInstitutionService;

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
public class EducationalInstitutionServiceImpl extends GenericServiceImpl<EducationalInstitution, UUID>
		implements EducationalInstitutionService {
	@Autowired
	EducationalInstitutionRepository educationalInstitutionRepository;
	@PersistenceContext
	EntityManager manager;

	@Override
	public EducationalInstitutionDto saveOrUpdate(EducationalInstitutionDto dto, UUID id) {
		if (dto != null) {
			EducationalInstitution educationalInstitution = null;
			if (id != null) {
				if (dto.getId() != null && !dto.getId().equals(id)) {
					return null;
				}
				Optional<EducationalInstitution> optional = educationalInstitutionRepository.findById(id);
				if (optional.isPresent()) {
					educationalInstitution = optional.get();
				}
				if (educationalInstitution != null) {
					educationalInstitution.setModifyDate(LocalDateTime.now());
				}
			}
			if (educationalInstitution == null) {
				educationalInstitution = new EducationalInstitution();
				educationalInstitution.setCreateDate(LocalDateTime.now());
				educationalInstitution.setModifyDate(LocalDateTime.now());
			}
			educationalInstitution.setName(dto.getName());
			educationalInstitution.setCode(dto.getCode());
			educationalInstitution.setNameEng(dto.getNameEng());
			educationalInstitution.setDescription(dto.getDescription());
			educationalInstitution = educationalInstitutionRepository.save(educationalInstitution);
			return new EducationalInstitutionDto(educationalInstitution);
		}
		return null;
	}

	@Override
	public void remove(UUID id) {
		EducationalInstitution entity = null;
		Optional<EducationalInstitution> optional = educationalInstitutionRepository.findById(id);
		if (optional.isPresent()) {
			entity = optional.get();
		}
		if (entity != null) {
			educationalInstitutionRepository.delete(entity);
		}
	}

	@Override
	public Page<EducationalInstitutionDto> searchByPage(SearchDto dto) {
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
		String orderBy = " ORDER BY entity.createDate ";

		String sqlCount = "select count(entity.id) from EducationalInstitution as entity where (1=1) ";
		String sql = "select new  com.globits.hr.dto.EducationalInstitutionDto(entity) from EducationalInstitution as entity where (1=1) ";

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
		}

		sql += whereClause + orderBy;
		sqlCount += whereClause;

		Query q = manager.createQuery(sql, EducationalInstitutionDto.class);
		Query qCount = manager.createQuery(sqlCount);

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			q.setParameter("text", '%' + dto.getKeyword() + '%');
			qCount.setParameter("text", '%' + dto.getKeyword() + '%');
		}

		int startPosition = pageIndex * pageSize;
		q.setFirstResult(startPosition);
		q.setMaxResults(pageSize);
		List<EducationalInstitutionDto> entities = q.getResultList();
		long count = (long) qCount.getSingleResult();

		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		return new PageImpl<>(entities, pageable, count);
	}

	@Override
	public EducationalInstitutionDto getEducationalInstitution(UUID id) {
		EducationalInstitution entity = null;
		Optional<EducationalInstitution> optional = educationalInstitutionRepository.findById(id);
		if (optional.isPresent()) {
			entity = optional.get();
		}
		if (entity != null) {
			return new EducationalInstitutionDto(entity);
		}
		return null;
	}

	@Override
	public Boolean checkCode(UUID id, String code) {
		List<EducationalInstitution> entities = educationalInstitutionRepository.findByCode(code);
		if (entities != null && entities.size() > 0 && entities.get(0) != null && entities.get(0).getId() != null) {
			if (id != null && StringUtils.hasText(id.toString())) {
				return !entities.get(0).getId().equals(id);
			}
			return true;
		}
		return false;
	}

	@Override
	public EducationalInstitutionDto findByCode(String code) {
		List<EducationalInstitution> entities = educationalInstitutionRepository.findByCode(code);
		if (entities == null || entities.size() == 0)
			return null;
		return new EducationalInstitutionDto(entities.get(0));
	}

}
