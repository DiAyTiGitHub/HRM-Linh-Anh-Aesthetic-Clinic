package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.CivilServantGrade;
import com.globits.hr.dto.CivilServantGradeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.CivilServantGradeRepository;
import com.globits.hr.service.CivilServantGradeService;

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
public class CivilServantGradeServiceImpl extends GenericServiceImpl<CivilServantGrade, UUID>
        implements CivilServantGradeService {
    @Autowired
    private CivilServantGradeRepository civilServantGradeRepository;

    @Override
    public CivilServantGradeDto saveOrUpdate(UUID id, CivilServantGradeDto dto) {
        if (dto != null) {
            LocalDateTime currentDate = LocalDateTime.now();
            String currentUserName = "Unknown User";
            CivilServantGrade civilServantGrade = null;
            if (dto.getId() != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<CivilServantGrade> civil = civilServantGradeRepository.findById(id);
                if (civil.isPresent()) {
                    civilServantGrade = civil.get();
                }
            }
            if (civilServantGrade == null) {
                civilServantGrade = new CivilServantGrade();
                civilServantGrade.setCreateDate(currentDate);
                civilServantGrade.setCreatedBy(currentUserName);
            }
            civilServantGrade.setModifiedBy(currentUserName);
            civilServantGrade.setModifyDate(currentDate);
            if (dto.getCode() != null) {
                civilServantGrade.setCode(dto.getCode());
            }
            if (dto.getName() != null) {
                civilServantGrade.setName(dto.getName());
            }
            if (dto.getDescription() != null) {
                civilServantGrade.setDescription(dto.getDescription());
            }
            civilServantGrade.setGrade(dto.getGrade());
            civilServantGrade.setNextGradeId(dto.getNextGradeId());
            civilServantGrade.setMaxGrade(dto.getMaxGrade());
            civilServantGrade.setSalaryCoefficient(dto.getSalaryCoefficient());
            civilServantGrade = civilServantGradeRepository.save(civilServantGrade);
            return new CivilServantGradeDto(civilServantGrade);
        }
        return null;
    }

    @Override
    public CivilServantGradeDto getCivilServantGrade(UUID id) {
        CivilServantGrade entity = null;
        Optional<CivilServantGrade> civil = civilServantGradeRepository.findById(id);
        if (civil.isPresent()) {
            entity = civil.get();
        }
        if (entity == null) {
            return null;
        } else {
            return new CivilServantGradeDto(entity);
        }
    }

    @Override
    public Boolean removeCivilServantGrade(UUID id) {
        CivilServantGrade civilServantGrade = null;
        Optional<CivilServantGrade> civil = civilServantGradeRepository.findById(id);
        if (civil.isPresent()) {
            civilServantGrade = civil.get();
        }
        if (civilServantGrade != null) {
            civilServantGradeRepository.delete(civilServantGrade);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<CivilServantGradeDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from CivilServantGrade as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.CivilServantGradeDto(entity) from CivilServantGrade as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.name LIKE :text  OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, CivilServantGradeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<CivilServantGradeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = civilServantGradeRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }
}
