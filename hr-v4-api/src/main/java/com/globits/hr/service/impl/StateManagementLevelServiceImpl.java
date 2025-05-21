package com.globits.hr.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.StateManagementLevel;
import com.globits.hr.dto.StateManagementLevelDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.StateManagementLevelRepository;
import com.globits.hr.service.StateManagementLevelService;

@Transactional
@Service
public class StateManagementLevelServiceImpl extends GenericServiceImpl<StateManagementLevel, UUID>
		implements StateManagementLevelService {
	@Autowired
	StateManagementLevelRepository stateManagementLevelRepository;

	@Override
	public StateManagementLevelDto saveStateManagementLevel(StateManagementLevelDto dto) {
//		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		 User modifiedUser;
//		 LocalDateTime currentDate = LocalDateTime.now();
//		 String currentUserName = "Unknown User";
//		 if (authentication != null) {
//			 modifiedUser = (User) authentication.getPrincipal();
//			 currentUserName = modifiedUser.getUsername();
//		 }

		StateManagementLevel stateManagementLevel = null;
		if (dto != null) {
			if (dto.getId() != null) {
//				Optional<StateManagementQualifications> optional = stateManagementQualificationsRepository
//						.findById(dto.getId());
//				if (optional.isPresent()) {
//					stateManagementQualifications = optional.get();
					if(dto.getId()!=null){			
						stateManagementLevel = stateManagementLevelRepository.getOne(dto.getId());
				}
			}
			if (stateManagementLevel == null) {// Nếu không tìm thấy thì tạo mới 1 đối tượng
				stateManagementLevel = new StateManagementLevel();
			}
			if (dto.getCode() != null) {
				stateManagementLevel.setCode(dto.getCode());
			}
			stateManagementLevel.setName(dto.getName());
			stateManagementLevel.setLevel(dto.getLevel());
			stateManagementLevel = stateManagementLevelRepository.save(stateManagementLevel);
			return new StateManagementLevelDto(stateManagementLevel);
		}
		return null;
	}

	@Override
	public Boolean deleteStateManagementLevel(UUID id) {
		StateManagementLevel stateManagementLevel = null;
		Optional<StateManagementLevel> optional = stateManagementLevelRepository.findById(id);
		if (optional.isPresent()) {
			stateManagementLevel = optional.get();
		}
		if (stateManagementLevel != null) {
			stateManagementLevelRepository.delete(stateManagementLevel);
			return true;
		}
		return false;
	}

	@Override
	public StateManagementLevelDto getStateManagementLevel(UUID id) {
		StateManagementLevel stateManagementLevel = null;
		Optional<StateManagementLevel> optional = stateManagementLevelRepository.findById(id);
		if (optional.isPresent()) {
			stateManagementLevel = optional.get();
		}
		if (stateManagementLevel != null) {
			return new StateManagementLevelDto(stateManagementLevel);
		}
		return null;
	}

//	@Override
//	public StateManagementQualificationsDto updateStateManagementQualifications(StateManagementQualificationsDto dto) {
//		if(dto != null) {
//			StateManagementQualifications updateStateManagementQualifications = null;
//			Optional<StateManagementQualifications> optional = stateManagementQualificationsRepository.findById(dto.getId());
//			if (optional.isPresent()) {
//				updateStateManagementQualifications = optional.get();
//			}
//			StateManagementQualifications a;
//			a = updateStateManagementQualifications;
//			if(a == null) {
//				a = new StateManagementQualifications();
//			}
//			a.setCode(dto.getCode());
//			a.setName(dto.getName());
//			a.setLevel(dto.getLevel());
//			a = stateManagementQualificationsRepository.save(a);
//			return new StateManagementQualificationsDto(a);
//		}
//		return null;
//	}

	@Override
	   public StateManagementLevelDto updateStateManagementLevel(StateManagementLevelDto dto,
																		  UUID id)
	{
		if (id != null) {
			if (dto != null) {
				if (dto.getId() != null && !id.equals(dto.getId())) {
					return null;
				}
				StateManagementLevel updateStateManagementLevel = null;
				Optional<StateManagementLevel> optional = stateManagementLevelRepository.findById(id);
				if (optional.isPresent()) {
					updateStateManagementLevel = optional.get();
				}
				StateManagementLevel a;
				a = updateStateManagementLevel;
				if (a == null) {
					a = new StateManagementLevel();
				}
				a.setCode(dto.getCode());
				a.setName(dto.getName());
				a.setLevel(dto.getLevel());
				a = stateManagementLevelRepository.save(a);
				return new StateManagementLevelDto(a);
			}
		}
		return null;
	}

	@Override
	public Page<StateManagementLevelDto> searchByPage(SearchDto dto) {
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

		String sqlCount = "select count(entity.id) from StateManagementLevel as entity where (1=1) ";
		String sql = "select new  com.globits.hr.dto.StateManagementLevelDto(entity) from StateManagementLevel as entity where (1=1) ";

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.level LIKE :text ) ";
		}

		sql += whereClause + orderBy;
		sqlCount += whereClause;

		Query q = manager.createQuery(sql, StateManagementLevelDto.class);
		Query qCount = manager.createQuery(sqlCount);

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			q.setParameter("text", '%' + dto.getKeyword() + '%');
			qCount.setParameter("text", '%' + dto.getKeyword() + '%');
		}

		int startPosition = pageIndex * pageSize;
		q.setFirstResult(startPosition);
		q.setMaxResults(pageSize);
		List<StateManagementLevelDto> entities = q.getResultList();
		long count = (long) qCount.getSingleResult();

		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		return new PageImpl<>(entities, pageable, count);
	}

	@Override
	public Boolean checkCode(UUID id, String code) {
		if (StringUtils.hasText(code)) {
			Long count = stateManagementLevelRepository.checkCode(code, id);
			return count != 0L;
		}
		return null;
	}
}
