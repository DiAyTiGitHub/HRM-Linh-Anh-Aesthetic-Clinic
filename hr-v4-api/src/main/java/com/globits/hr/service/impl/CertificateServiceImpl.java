package com.globits.hr.service.impl;

import com.globits.hr.domain.Certificate;
import com.globits.hr.dto.CertificateDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.CertificateRepository;
import com.globits.hr.service.CertificateService;

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
public class CertificateServiceImpl implements CertificateService {
    @PersistenceContext
    EntityManager manager;

    @Autowired
    CertificateRepository repos;

    @Override
    public CertificateDto saveOrUpdate(UUID id, CertificateDto dto) {
        if (dto != null && dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
            Certificate entity = null;
            if (dto.getId() != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<Certificate> certificateOptional = repos.findById(id);
                if (certificateOptional.isPresent()) {
                    entity = certificateOptional.get();
                }
                if (entity != null)
                    entity.setModifyDate(LocalDateTime.now());
            }
            if (entity == null) {
                entity = new Certificate();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setType(dto.getType());

            entity = repos.save(entity);
            return new CertificateDto(entity);
        }
        return null;
    }

    @Override
    public Boolean delete(UUID id) {
        if (id != null) {
            repos.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public CertificateDto getCertificate(UUID id) {
        Certificate entity = null;
        Optional<Certificate> certificateOptional = repos.findById(id);
        if (certificateOptional.isPresent()) {
            entity = certificateOptional.get();
        }
        if (entity != null) {
            return new CertificateDto(entity);
        }
        return null;
    }

    @Override
    public Page<CertificateDto> searchByPage(SearchDto dto) {
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

        String orderBy = " ORDER BY entity.createDate DESC";

        String sqlCount = "select count(entity.id) from Certificate as entity where (1=1)   ";
        String sql = "select new com.globits.hr.dto.CertificateDto(entity) from Certificate as entity where (1=1)  ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text )";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, CertificateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<CertificateDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public CertificateDto findByCode(String code) {
        List<Certificate> entities = repos.findByCode(code);
        if (entities == null || entities.size() == 0) return null;
        return new CertificateDto(entities.get(0));
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = repos.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }
}
