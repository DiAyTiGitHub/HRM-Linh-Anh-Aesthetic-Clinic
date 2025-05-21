
package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.ProfessionalDegree;
import com.globits.hr.dto.ProfessionalDegreeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.ProfessionalDegreeRepository;
import com.globits.hr.service.ProfessionalDegreeService;

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
public class ProfessionalDegreeServiceImpl extends GenericServiceImpl<ProfessionalDegree, UUID>
        implements ProfessionalDegreeService {

    @Autowired
    ProfessionalDegreeRepository professionalDegreeRepository;

    @Override
    public ProfessionalDegreeDto saveProfessionalDegree(ProfessionalDegreeDto dto) {
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        ProfessionalDegree professionalDegree = null;
        if (dto != null) {
            if (dto.getId() != null) {
                Optional<ProfessionalDegree> optional = professionalDegreeRepository.findById(dto.getId());
                if (optional.isPresent()) {
                    professionalDegree = optional.get();
                }
            }
            if (professionalDegree == null) {
                professionalDegree = new ProfessionalDegree();
                professionalDegree.setCreateDate(currentDate);
                professionalDegree.setCreatedBy(currentUserName);
            }
            if (dto.getCode() != null) {
                professionalDegree.setCode(dto.getCode());
            }
            professionalDegree.setName(dto.getName());
            professionalDegree.setModifyDate(currentDate);
            professionalDegree.setModifiedBy(currentUserName);
            professionalDegree = professionalDegreeRepository.save(professionalDegree);
            return new ProfessionalDegreeDto(professionalDegree);
        }
        return null;
    }

    @Override
    public Boolean deleteProfessionalDegree(UUID id) {
        ProfessionalDegree professionalDegree = null;
        Optional<ProfessionalDegree> optional = professionalDegreeRepository.findById(id);
        if (optional.isPresent()) {
            professionalDegree = optional.get();
        }
        if (professionalDegree != null) {
            professionalDegreeRepository.delete(professionalDegree);
            return true;
        }
        return false;
    }

    @Override
    public ProfessionalDegreeDto getProfessionalDegree(UUID id) {
        ProfessionalDegree professionalDegree = null;
        Optional<ProfessionalDegree> optional = professionalDegreeRepository.findById(id);
        if (optional.isPresent()) {
            professionalDegree = optional.get();
        }
        if (professionalDegree != null) {
            return new ProfessionalDegreeDto(professionalDegree);
        }
        return null;
    }

    @Override
    public ProfessionalDegreeDto updateProfessionalDegree(ProfessionalDegreeDto dto) {
        if (dto != null) {
            ProfessionalDegree updateProfessionalDegree = null;
            Optional<ProfessionalDegree> optional = professionalDegreeRepository.findById(dto.getId());
            if (optional.isPresent()) {
                updateProfessionalDegree = optional.get();
            }
            ProfessionalDegree professionalDegree;
            professionalDegree = updateProfessionalDegree;
            if (professionalDegree == null) {
                professionalDegree = new ProfessionalDegree();
            }
            professionalDegree.setCode(dto.getCode());
            professionalDegree.setName(dto.getName());
            professionalDegree = professionalDegreeRepository.save(professionalDegree);
            return new ProfessionalDegreeDto(professionalDegree);
        }
        return null;
    }

    @Override
    public Page<ProfessionalDegreeDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from ProfessionalDegree as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.ProfessionalDegreeDto(entity) from ProfessionalDegree as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, ProfessionalDegreeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<ProfessionalDegreeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = professionalDegreeRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }
}
