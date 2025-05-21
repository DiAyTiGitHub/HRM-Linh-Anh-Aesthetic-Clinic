package com.globits.hr.service.impl;

import java.time.LocalDateTime;
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
import com.globits.hr.domain.TitleConferred;
import com.globits.hr.dto.AcademicTitleDto;
import com.globits.hr.dto.TitleConferredDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.TitleConferredRepository;
import com.globits.hr.service.TitleConferredService;
@Transactional
@Service
public class TitleConferredServiceImpl extends GenericServiceImpl<TitleConferred, UUID> implements TitleConferredService{	
	    @Autowired
	    TitleConferredRepository titleConferredRepository;
	    @Override
	    public Boolean deleteTitleConferred(UUID id) {
	    	TitleConferred titleConferred = null;
	        Optional<TitleConferred> titleConferredOptional = titleConferredRepository.findById(id);
	        if (titleConferredOptional.isPresent()) {
	        	titleConferred = titleConferredOptional.get();
	        }
	        if (titleConferred != null) {
	        	titleConferredRepository.delete(titleConferred);
	            return true;
	        }
	        return false;
	    }

	    @Override
	    public TitleConferredDto getTitleConferred(UUID id) {
	    	TitleConferred titleConferred = null;
	        Optional<TitleConferred> titleConferredOptional = titleConferredRepository.findById(id);
	        if (titleConferredOptional.isPresent()) {
	        	titleConferred = titleConferredOptional.get();
	        }
	        if (titleConferred != null) {
	            return new TitleConferredDto(titleConferred);
	        }
	        return null;
	    }

	    @Override
	    public Page<TitleConferredDto> searchByPage(SearchDto dto) {
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

	        String sqlCount = "select count(entity.id) from TitleConferred as entity where (1=1) ";
	        String sql = "select new  com.globits.hr.dto.TitleConferredDto(entity) from TitleConferred as entity where (1=1) ";

	        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
	            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
	        }

	        sql += whereClause + orderBy;
	        sqlCount += whereClause;

	        Query q = manager.createQuery(sql, TitleConferredDto.class);
	        Query qCount = manager.createQuery(sqlCount);

	        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
	            q.setParameter("text", '%' + dto.getKeyword() + '%');
	            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
	        }

	        int startPosition = pageIndex * pageSize;
	        q.setFirstResult(startPosition);
	        q.setMaxResults(pageSize);
	        List<TitleConferredDto> entities = q.getResultList();
	        long count = (long) qCount.getSingleResult();

	        Pageable pageable = PageRequest.of(pageIndex, pageSize);
	        return new PageImpl<>(entities, pageable, count);
	    }

	    @Override
	    public Boolean checkCode(UUID id, String code) {
	        if (StringUtils.hasText(code)) {
	            Long count = titleConferredRepository.checkCode(code, id);
	            return count != 0L;
	        }
	        return null;
	    }

	    @Override
	    public TitleConferredDto saveOrUpdate(UUID id, TitleConferredDto dto) {
	        if (dto != null) {
	        	TitleConferred entity = null;
	            if (id != null) {
	                if (dto.getId() != null && !dto.getId().equals(id)) {
	                    return null;
	                }
	                Optional<TitleConferred> titleConferred = titleConferredRepository.findById(id);
	                if (titleConferred.isPresent()) {
	                    entity = titleConferred.get();
	                }
	                if (entity != null) {
	                    entity.setModifyDate(LocalDateTime.now());
	                }
	            }
	            if (entity == null) {
	                entity = new TitleConferred();
	                entity.setCreateDate(LocalDateTime.now());
	                entity.setModifyDate(LocalDateTime.now());
	            }
	            entity.setCode(dto.getCode());
	            entity.setName(dto.getName());
	            entity.setDescription(dto.getDescription());
	            entity.setLevel(dto.getLevel());
	            entity = titleConferredRepository.save(entity);
	            return new TitleConferredDto(entity);
	        }
	        return null;
	    }
	    @Override
		public Page<TitleConferredDto> getPage(int pageSize, int pageIndex) {
			Pageable pageable = PageRequest.of(pageIndex-1, pageSize);
			return titleConferredRepository.getListPage(pageable);
		}
}
