package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.PoliticalTheoryLevel;
import com.globits.hr.dto.PoliticalTheoryLevelDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.PoliticalTheoryLevelRepository;
import com.globits.hr.service.PoliticalTheoryLevelService;
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

@Transactional
@Service
public class PoliticalTheoryLevelServiceImpl extends GenericServiceImpl<PoliticalTheoryLevel, UUID>  implements PoliticalTheoryLevelService {
	@Autowired
	PoliticalTheoryLevelRepository politicalTheoryLevelRepository;

	@Override
	public PoliticalTheoryLevelDto savePoliticalTheoryLevel(PoliticalTheoryLevelDto dto) {
//		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		 User modifiedUser;
//		 LocalDateTime currentDate = LocalDateTime.now();
//		 String currentUserName = "Unknown User";
//		 if (authentication != null) {
//			 modifiedUser = (User) authentication.getPrincipal();
//			 currentUserName = modifiedUser.getUsername();
//		 }
		 
		 PoliticalTheoryLevel politicalTheoryLevel = null;
		if(dto!=null) {
			if(dto.getId()!=null){
				politicalTheoryLevel = politicalTheoryLevelRepository.getOne(dto.getId());
//				Optional<PoliticalTheoryLevel> optional = politicalTheoryLevelRepository.findById(dto.getId());
//				if (optional.isPresent()) {
//					politicalTheoryLevel = optional.get();
//				}
			}
			if(politicalTheoryLevel == null) {//Nếu không tìm thấy thì tạo mới 1 đối tượng
				politicalTheoryLevel = new PoliticalTheoryLevel();
			
			}
			if(dto.getCode()!=null) {
				politicalTheoryLevel.setCode(dto.getCode());
			}
			politicalTheoryLevel.setName(dto.getName());
			politicalTheoryLevel.setLevel(dto.getLevel());
		
			politicalTheoryLevel = politicalTheoryLevelRepository.save(politicalTheoryLevel);
			return new PoliticalTheoryLevelDto(politicalTheoryLevel);
		}
		return null;
	}

	@Override
	public Boolean deletePoliticalTheoryLevel(UUID id) {
		PoliticalTheoryLevel politicalTheoryLevel = null;
		Optional<PoliticalTheoryLevel> optional = politicalTheoryLevelRepository.findById(id);
		if (optional.isPresent()) {
			politicalTheoryLevel = optional.get();
		}
		if(politicalTheoryLevel!=null) {
			politicalTheoryLevelRepository.delete(politicalTheoryLevel);
			return true;
		}
		return false;
	}

	@Override
	public PoliticalTheoryLevelDto getPoliticalTheoryLevel(UUID id) {
		PoliticalTheoryLevel politicalTheoryLevel = null;
		Optional<PoliticalTheoryLevel> optional = politicalTheoryLevelRepository.findById(id);
		if (optional.isPresent()) {
			politicalTheoryLevel = optional.get();
		}
		if(politicalTheoryLevel!=null) {
			return new PoliticalTheoryLevelDto(politicalTheoryLevel);
		}
		return null;
	}

//	@Override
//	public PoliticalTheoryLevelDto updatePoliticalTheoryLevel(PoliticalTheoryLevelDto dto) {
//		if(dto != null) {
//			PoliticalTheoryLevel updatePoliticalTheoryLevel = null;
//			Optional<PoliticalTheoryLevel> optional = politicalTheoryLevelRepository.findById(dto.getId());
//			if (optional.isPresent()) {
//				updatePoliticalTheoryLevel = optional.get();
//			}
//			PoliticalTheoryLevel a;
//			a = updatePoliticalTheoryLevel;
//			if(a == null) {
//				a = new PoliticalTheoryLevel();
//			}
//			a.setCode(dto.getCode());
//			a.setName(dto.getName());
//			a.setLevel(dto.getLevel());
//			a = politicalTheoryLevelRepository.save(a);
//			return new PoliticalTheoryLevelDto(a);
//		}
//		return null;
//	}
    @Override
    public PoliticalTheoryLevelDto updatePoliticalTheoryLevel(PoliticalTheoryLevelDto dto, UUID id) {
        if (id != null) {
            if (dto != null) {
                if (dto.getId() != null && !id.equals(dto.getId())) {
                    return null;
                }
                PoliticalTheoryLevel updatePoliticalTheoryLevel = null;
                Optional<PoliticalTheoryLevel> optional = politicalTheoryLevelRepository.findById(id);
                if (optional.isPresent()) {
                    updatePoliticalTheoryLevel = optional.get();
                }
                PoliticalTheoryLevel a;
                a = updatePoliticalTheoryLevel;
                if (a == null) {
                    a = new PoliticalTheoryLevel();
                }
                a.setCode(dto.getCode());
                a.setName(dto.getName());
                a.setLevel(dto.getLevel());
                a = politicalTheoryLevelRepository.save(a);
                return new PoliticalTheoryLevelDto(a);
            }
        }
        return null;
    }
	
	@Override
	public Page<PoliticalTheoryLevelDto> searchByPage(SearchDto dto) {
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
		
		String sqlCount = "select count(entity.id) from PoliticalTheoryLevel as entity where (1=1) ";
		String sql = "select new  com.globits.hr.dto.PoliticalTheoryLevelDto(entity) from PoliticalTheoryLevel as entity where (1=1) ";

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
		}
		
		
		sql+=whereClause + orderBy;
		sqlCount+=whereClause;

		Query q = manager.createQuery(sql, PoliticalTheoryLevelDto.class);
		Query qCount = manager.createQuery(sqlCount);

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			q.setParameter("text", '%' + dto.getKeyword() + '%');
			qCount.setParameter("text", '%' + dto.getKeyword() + '%');
		}
		

		int startPosition = pageIndex * pageSize;
		q.setFirstResult(startPosition);
		q.setMaxResults(pageSize);
		List<PoliticalTheoryLevelDto> entities = q.getResultList();
		long count = (long) qCount.getSingleResult();
		
		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		return new PageImpl<>(entities, pageable, count);
	}
	
	@Override
	public Boolean checkCode(UUID id,String code) {
		if (StringUtils.hasText(code)) {
            Long count = politicalTheoryLevelRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
	}
}
