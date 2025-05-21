package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.CivilServantCategoryGrade;
import com.globits.hr.dto.CivilServantCategoryGradeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.CivilServantCategoryGradeRepository;
import com.globits.hr.service.CivilServantCategoryGradeService;
import com.globits.security.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class CivilServantCategoryGradeServiceImpl extends GenericServiceImpl<CivilServantCategoryGrade, UUID>
        implements CivilServantCategoryGradeService {
    @Autowired
    private CivilServantCategoryGradeRepository civilServantCategoryGradeRepository;

    @Override
    public CivilServantCategoryGradeDto saveOrUpdate(UUID id, CivilServantCategoryGradeDto dto) {
        if (dto != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User modifiedUser;
            LocalDateTime currentDate = LocalDateTime.now();
            String currentUserName = "Unknown User";
            if (authentication != null) {
                modifiedUser = (User) authentication.getPrincipal();
                currentUserName = modifiedUser.getUsername();
            }

            CivilServantCategoryGrade civilServantCategoryGrade = null;
            if (dto.getId() != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<CivilServantCategoryGrade> civil = civilServantCategoryGradeRepository.findById(id);
                if (civil.isPresent()) {
                    civilServantCategoryGrade = civil.get();
                }

            }
            if (civilServantCategoryGrade == null) {
                civilServantCategoryGrade = new CivilServantCategoryGrade();
                civilServantCategoryGrade.setCreateDate(currentDate);
                civilServantCategoryGrade.setCreatedBy(currentUserName);
            }
            civilServantCategoryGrade.setModifiedBy(currentUserName);
            civilServantCategoryGrade.setModifyDate(currentDate);
            if (dto.getCode() != null) {
                civilServantCategoryGrade.setCode(dto.getCode());
            }
            if (dto.getName() != null) {
                civilServantCategoryGrade.setName(dto.getName());
            }
            civilServantCategoryGrade.setSalaryCoefficient(dto.getSalaryCoefficient());
            if (dto.getCivilServantCategory() != null) {
                civilServantCategoryGrade.setCivilServantCategory(dto.getCivilServantCategory());
            }
            if (dto.getCivilServantGrade() != null) {
                civilServantCategoryGrade.setCivilServantGrade(dto.getCivilServantGrade());
            }
            civilServantCategoryGrade.setVoided(dto.getVoided());
            civilServantCategoryGrade = civilServantCategoryGradeRepository.save(civilServantCategoryGrade);
            return new CivilServantCategoryGradeDto(civilServantCategoryGrade);
        }
        return null;
    }

    @Override
    public CivilServantCategoryGradeDto getCivilServantCategoryGrade(UUID id) {
        CivilServantCategoryGrade entity = null;
        Optional<CivilServantCategoryGrade> civil = civilServantCategoryGradeRepository.findById(id);
        if (civil.isPresent()) {
            entity = civil.get();
        }
        if (entity == null) {
            return null;
        }
        return new CivilServantCategoryGradeDto(entity);
    }

    @Override
    public Boolean removeCivilServantCategoryGrade(UUID id) {
        CivilServantCategoryGrade civilServantCategory = null;
        Optional<CivilServantCategoryGrade> civil = civilServantCategoryGradeRepository.findById(id);
        if (civil.isPresent()) {
            civilServantCategory = civil.get();
        }
        if (civilServantCategory != null) {
            civilServantCategoryGradeRepository.delete(civilServantCategory);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<CivilServantCategoryGradeDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from CivilServantCategoryGrade as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.CivilServantCategoryGradeDto(entity) from CivilServantCategoryGrade as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.salaryCoefficient LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, CivilServantCategoryGradeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<CivilServantCategoryGradeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = civilServantCategoryGradeRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }
}
