package com.globits.hr.service.impl;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Language;
import com.globits.hr.dto.LanguageDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.LanguageRepository;
import com.globits.hr.service.LanguageService;

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
public class LanguageServiceImpl extends GenericServiceImpl<Language, UUID> implements LanguageService {
    @Autowired
    LanguageRepository otherLanguageRepository;
    @Override
    public LanguageDto saveOrUpdate( UUID id, LanguageDto dto) {
        if (dto != null) {
            Language otherLanguage = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<Language> optional = otherLanguageRepository.findById(id);
                if (optional.isPresent()) {
                    otherLanguage = optional.get();
                }
                if (otherLanguage != null) {
                    otherLanguage.setModifyDate(LocalDateTime.now());
                }
            }
            if (otherLanguage == null) {
                otherLanguage = new Language();
                otherLanguage.setModifyDate(LocalDateTime.now());
                otherLanguage.setCreateDate(LocalDateTime.now());
            }
            otherLanguage.setName(dto.getName());
            otherLanguage.setCode(dto.getCode());
            otherLanguage = otherLanguageRepository.save(otherLanguage);
            return new LanguageDto(otherLanguage);
        }
        return null;
    }
    @Override
    public Boolean deleteOtherLanguage(UUID id) {
        Language otherLanguage = null;
        Optional<Language> otherLanguageOptional = otherLanguageRepository.findById(id);
        if (otherLanguageOptional.isPresent()) {
            otherLanguage = otherLanguageOptional.get();
        }
        if (otherLanguage != null) {
            otherLanguageRepository.delete(otherLanguage);
            return true;
        }
        return false;
    }

    @Override
    public LanguageDto getOtherLanguage(UUID id) {
        Language otherLanguage = null;
        Optional<Language> otherLanguageOptional = otherLanguageRepository.findById(id);
        if (otherLanguageOptional.isPresent()) {
            otherLanguage = otherLanguageOptional.get();
        }
        if (otherLanguage != null) {
            return new LanguageDto(otherLanguage);
        }
        return null;
    }

    @Override
    public Page<LanguageDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from Language as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.LanguageDto(entity) from Language as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, LanguageDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<LanguageDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = otherLanguageRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }
    @Override
    public Page<LanguageDto> getPage(int pageSize, int pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex-1, pageSize);
        return otherLanguageRepository.getListPage(pageable);
    }
}
