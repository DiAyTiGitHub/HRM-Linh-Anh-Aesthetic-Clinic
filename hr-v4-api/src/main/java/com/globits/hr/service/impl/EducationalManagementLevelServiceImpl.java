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
import com.globits.hr.domain.EducationalManagementLevel;
import com.globits.hr.dto.EducationalManagementLevelDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.EducationalManagementLevelRepository;
import com.globits.hr.service.EducationalManagementLevelService;

@Transactional
@Service
public class EducationalManagementLevelServiceImpl extends GenericServiceImpl<EducationalManagementLevel,UUID> implements EducationalManagementLevelService {
	@Autowired
    EducationalManagementLevelRepository educationalManagementLevelRepository;

	@Override
	public EducationalManagementLevelDto saveEducationalManagementLevel(EducationalManagementLevelDto dto) {
//		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		 User modifiedUser;
//		 LocalDateTime currentDate = LocalDateTime.now();
//		 String currentUserName = "Unknown User";
//		 if (authentication != null) {
//			 modifiedUser = (User) authentication.getPrincipal();
//			 currentUserName = modifiedUser.getUsername();
//		 }
		 
		 EducationalManagementLevel educationalManagementLevel = null;
		if(dto!=null) {
			if(dto.getId()!=null){			
			educationalManagementLevel = educationalManagementLevelRepository.getOne(dto.getId());
//				Optional<EducationalManagementQualifications> optional = educationalManagementLevelRepository.findById(dto.getId());
//				if (optional.isPresent()) {
//					educationalManagementLevel = optional.get();
				}
			
			if(educationalManagementLevel == null) {//Nếu không tìm thấy thì tạo mới 1 đối tượng
				educationalManagementLevel = new EducationalManagementLevel();

			}
			if(dto.getCode()!=null) {
				educationalManagementLevel.setCode(dto.getCode());
			}
			educationalManagementLevel.setName(dto.getName());
			educationalManagementLevel.setLevel(dto.getLevel());
			educationalManagementLevel = educationalManagementLevelRepository.save(educationalManagementLevel);
			return new EducationalManagementLevelDto(educationalManagementLevel);
		}
		return null;
	}

	@Override
	public Boolean deleteEducationalManagementLevel(UUID id) {
		EducationalManagementLevel educationalManagementLevel = null;
		Optional<EducationalManagementLevel> optional = educationalManagementLevelRepository.findById(id);
		if (optional.isPresent()) {
			educationalManagementLevel = optional.get();
		}
		if(educationalManagementLevel !=null) {
			educationalManagementLevelRepository.delete(educationalManagementLevel);
			return true;
		}
		return false;
	}

	@Override
	public EducationalManagementLevelDto getEducationalManagementLevel(UUID id) {
		EducationalManagementLevel educationalManagementLevel = null;
		Optional<EducationalManagementLevel> optional = educationalManagementLevelRepository.findById(id);
		if (optional.isPresent()) {
			educationalManagementLevel = optional.get();
		}
		if(educationalManagementLevel !=null) {
			return new EducationalManagementLevelDto(educationalManagementLevel);
		}
		return null;
	}

//	@Override
//	public EducationalManagementQualificationsDto updateEducationalManagementQualifications(EducationalManagementQualificationsDto dto) {
//		if(dto != null) {
//			EducationalManagementQualifications updateEducationalManagementQualifications = null;
//			Optional<EducationalManagementQualifications> optional = educationalManagementQualificationslRepository.findById(dto.getId());
//			if (optional.isPresent()) {
//				updateEducationalManagementQualifications = optional.get();
//			}
//			EducationalManagementQualifications a;
//			a = updateEducationalManagementQualifications;
//			if(a == null) {
//				a = new EducationalManagementQualifications();
//			}
//			a.setCode(dto.getCode());
//			a.setName(dto.getName());
//			a.setLevel(dto.getLevel());
//			a = educationalManagementQualificationslRepository.save(a);
//			return new EducationalManagementQualificationsDto(a);
//		}
//		return null;
//	}
	  @Override
	    public EducationalManagementLevelDto updateEducationalManagementLevel(EducationalManagementLevelDto dto, UUID id) {
	        if (id != null) {
	            if (dto != null) {
	                if (dto.getId() != null && !id.equals(dto.getId())) {
	                    return null;
	                }
	                EducationalManagementLevel updateEducationalManagementLevel = null;
	                Optional<EducationalManagementLevel> optional = educationalManagementLevelRepository.findById(id);
	                if (optional.isPresent()) {
	                    updateEducationalManagementLevel = optional.get();
	                }
	                EducationalManagementLevel a;
	                a = updateEducationalManagementLevel;
	                if (a == null) {
	                    a = new EducationalManagementLevel();
	                }
	                a.setCode(dto.getCode());
	                a.setName(dto.getName());
	                a.setLevel(dto.getLevel());
	                a = educationalManagementLevelRepository.save(a);
	                return new EducationalManagementLevelDto(a);
	            }
	        }
	        return null;
	    }


	@Override
	public Page<EducationalManagementLevelDto> searchByPage(SearchDto dto) {
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
		
		String sqlCount = "select count(entity.id) from EducationalManagementLevel as entity where (1=1) ";
		String sql = "select new  com.globits.hr.dto.EducationalManagementLevelDto(entity) from EducationalManagementLevel as entity where (1=1) ";

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.level LIKE :text ) ";
		}
		
		
		sql+=whereClause + orderBy;
		sqlCount+=whereClause;

		Query q = manager.createQuery(sql, EducationalManagementLevelDto.class);
		Query qCount = manager.createQuery(sqlCount);

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			q.setParameter("text", '%' + dto.getKeyword() + '%');
			qCount.setParameter("text", '%' + dto.getKeyword() + '%');
		}
		

		int startPosition = pageIndex * pageSize;
		q.setFirstResult(startPosition);
		q.setMaxResults(pageSize);
		List<EducationalManagementLevelDto> entities = q.getResultList();
		long count = (long) qCount.getSingleResult();
		
		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		return new PageImpl<>(entities, pageable, count);
	}
	
	@Override
	public Boolean checkCode(UUID id,String code) {
		if (StringUtils.hasText(code)) {
            Long count = educationalManagementLevelRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
	}
}
