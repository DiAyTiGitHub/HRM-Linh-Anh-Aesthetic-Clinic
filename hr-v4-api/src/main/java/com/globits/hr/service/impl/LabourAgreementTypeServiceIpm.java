package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.LabourAgreementType;
import com.globits.hr.dto.LabourAgreementTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.LabourAgreementTypeRepository;
import com.globits.hr.service.LabourAgreementTypeService;

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
public class LabourAgreementTypeServiceIpm extends GenericServiceImpl<LabourAgreementType, UUID>
        implements LabourAgreementTypeService {
    @Autowired
    LabourAgreementTypeRepository labourAgreementRepository;

    @Override
    public Page<LabourAgreementTypeDto> searchByPage(SearchDto dto) {
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

        String orderBy = " ORDER BY entity.code DESC";

        String sqlCount = "select count(entity.id) from LabourAgreementType as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.LabourAgreementTypeDto(entity) from LabourAgreementType as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.code) LIKE UPPER(:text) ) OR ( UPPER(entity.name) LIKE UPPER(:text) )";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, LabourAgreementTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<LabourAgreementTypeDto> entities = q.getResultList();
//        if (null != entities && entities.size() > 0) {
//            entities.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));
//        }
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        if (entities != null) {
            return new PageImpl<>(entities, pageable, count);
        }
        return null;
    }

    @Override
    public LabourAgreementTypeDto saveOrUpdate(LabourAgreementTypeDto dto, UUID id) {
        if (dto != null) {
            LocalDateTime currentDate = LocalDateTime.now();
            String currentUserName = "Unknown User";
            LabourAgreementType entity = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<LabourAgreementType> optional = labourAgreementRepository.findById(id);
                if (optional.isPresent()) {
                    entity = optional.get();
                }
                if (entity != null) {
                    entity.setModifyDate(currentDate);
                    entity.setModifiedBy(currentUserName);
                }
            }
            if (entity == null) {
                entity = new LabourAgreementType();
                entity.setCreateDate(currentDate);
                entity.setModifyDate(currentDate);
                entity.setModifiedBy(currentUserName);
                entity.setCreatedBy(currentUserName);
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setLanguageKey(dto.getLanguageKey());
            entity = labourAgreementRepository.save(entity);
            return new LabourAgreementTypeDto(entity);
        }
        return null;
    }

    @Override
    public LabourAgreementTypeDto getOne(UUID id) {
        if (id != null) {
            LabourAgreementType entity = null;
            Optional<LabourAgreementType> optional = labourAgreementRepository.findById(id);
            if (optional.isPresent()) {
                entity = optional.get();
            }
            if (entity != null) {
                return new LabourAgreementTypeDto(entity);
            }
        }
        return null;
    }

    @Override
    public void deleteOne(UUID id) {
        LabourAgreementType entity = null;
        Optional<LabourAgreementType> optional = labourAgreementRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            labourAgreementRepository.delete(entity);
        }
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        List<LabourAgreementType> entities = labourAgreementRepository.findByCode(code);
        if (entities != null && entities.size() > 0 && entities.get(0) != null && entities.get(0).getId() != null) {
            if (id != null && StringUtils.hasText(id.toString())) {
                return !entities.get(0).getId().equals(id);
            }
            return true;
        }
        return false;
    }
}
