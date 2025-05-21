/*
 * TA va Giang làm
 */

package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.AcademicTitle;
import com.globits.hr.dto.AcademicTitleDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.AcademicTitleRepository;
import com.globits.hr.service.AcademicTitleService;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class AcademicTitleServiceImpl extends GenericServiceImpl<AcademicTitle, UUID> implements AcademicTitleService {

    @Autowired
    AcademicTitleRepository academicTitleRepository;

    @Override
    public Boolean deleteAcademicTitle(UUID id) {
        AcademicTitle academicTitle = null;
        Optional<AcademicTitle> academicTitleOptional = academicTitleRepository.findById(id);
        if (academicTitleOptional.isPresent()) {
            academicTitle = academicTitleOptional.get();
        }
        if (academicTitle != null) {
            academicTitleRepository.delete(academicTitle);
            return true;
        }
        return false;
    }

    @Override
    public AcademicTitleDto getAcademicTitle(UUID id) {
        AcademicTitle academicTitle = null;
        Optional<AcademicTitle> academicTitleOptional = academicTitleRepository.findById(id);
        if (academicTitleOptional.isPresent()) {
            academicTitle = academicTitleOptional.get();
        }
        if (academicTitle != null) {
            return new AcademicTitleDto(academicTitle);
        }
        return null;
    }

    @Override
    public Page<AcademicTitleDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from AcademicTitle as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.AcademicTitleDto(entity) from AcademicTitle as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, AcademicTitleDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<AcademicTitleDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = academicTitleRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }

    @Override
    public AcademicTitleDto saveOrUpdate(UUID id, AcademicTitleDto dto) {
        if (dto != null) {
            AcademicTitle entity = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<AcademicTitle> academicTitle = academicTitleRepository.findById(id);
                if (academicTitle.isPresent()) {
                    entity = academicTitle.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new AcademicTitle();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity = academicTitleRepository.save(entity);
            return new AcademicTitleDto(entity);
        }
        return null;
    }
    @Override
	public Page<AcademicTitleDto> getPage(int pageSize, int pageIndex) {
		Pageable pageable = PageRequest.of(pageIndex-1, pageSize);
		return academicTitleRepository.getListPage(pageable);
	}
}
