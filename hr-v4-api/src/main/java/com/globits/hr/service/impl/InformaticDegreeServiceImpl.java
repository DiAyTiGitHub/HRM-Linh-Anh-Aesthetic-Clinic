package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.InformaticDegree;
import com.globits.hr.dto.InformaticDegreeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.InformaticDegreeRepository;
import com.globits.hr.service.InformaticDegreeService;

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
public class InformaticDegreeServiceImpl extends GenericServiceImpl<InformaticDegree, UUID>
        implements InformaticDegreeService {
    @Autowired
    InformaticDegreeRepository informaticDegreeRepository;

    @Override
    public InformaticDegreeDto saveInformaticDegree(InformaticDegreeDto dto) {
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";

        InformaticDegree informaticDegree = null;
        if (dto != null) {
            if (dto.getId() != null)
                informaticDegree = informaticDegreeRepository.getOne(dto.getId());
            if (informaticDegree == null) {
                informaticDegree = new InformaticDegree();
                informaticDegree.setCreateDate(currentDate);
                informaticDegree.setCreatedBy(currentUserName);
            }
            if (dto.getCode() != null) {
                informaticDegree.setCode(dto.getCode());
            }
            informaticDegree.setName(dto.getName());
            informaticDegree.setModifyDate(currentDate);
            informaticDegree.setModifiedBy(currentUserName);
            informaticDegree = informaticDegreeRepository.save(informaticDegree);
            return new InformaticDegreeDto(informaticDegree);
        }
        return null;
    }

    @Override
    public Boolean deleteInformaticDegree(UUID id) {
        InformaticDegree informaticDegree = null;
        Optional<InformaticDegree> optional = informaticDegreeRepository.findById(id);
        if (optional.isPresent()) {
            informaticDegree = optional.get();
        }
        if (informaticDegree != null) {
            informaticDegreeRepository.delete(informaticDegree);
            return true;
        }
        return false;
    }

    @Override
    public InformaticDegreeDto getInformaticDegree(UUID id) {
        InformaticDegree informaticDegree = null;
        Optional<InformaticDegree> optional = informaticDegreeRepository.findById(id);
        if (optional.isPresent()) {
            informaticDegree = optional.get();
        }
        if (informaticDegree != null) {
            return new InformaticDegreeDto(informaticDegree);
        }
        return null;
    }

    @Override
    public InformaticDegreeDto updateInformaticDegree(InformaticDegreeDto dto, UUID id) {
        if (id != null) {
            if (dto != null) {
                if (dto.getId() != null && !id.equals(dto.getId())) {
                    return null;
                }
                InformaticDegree updateInformaticDegree = null;
                Optional<InformaticDegree> optional = informaticDegreeRepository.findById(id);
                if (optional.isPresent()) {
                    updateInformaticDegree = optional.get();
                }
                InformaticDegree a;
                a = updateInformaticDegree;
                if (a == null) {
                    a = new InformaticDegree();
                }
                a.setCode(dto.getCode());
                a.setName(dto.getName());
                a = informaticDegreeRepository.save(a);
                return new InformaticDegreeDto(a);
            }
        }
        return null;
    }

    @Override
    public Page<InformaticDegreeDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from InformaticDegree as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.InformaticDegreeDto(entity) from InformaticDegree as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, InformaticDegreeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<InformaticDegreeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = informaticDegreeRepository.countByCode(code, id);
            return count != 0L;
        }
        return null;
    }
}
