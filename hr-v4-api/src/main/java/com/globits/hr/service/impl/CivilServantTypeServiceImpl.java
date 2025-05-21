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
import com.globits.hr.domain.CivilServantType;
import com.globits.hr.dto.CivilServantTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.CivilServantTypeRepository;
import com.globits.hr.service.CivilServantTypeService;

@Transactional
@Service
public class CivilServantTypeServiceImpl extends GenericServiceImpl<CivilServantType, UUID>
        implements CivilServantTypeService {

    @Autowired
    private CivilServantTypeRepository civilServantTypeRepository;

    @Override
    public CivilServantTypeDto getOne(UUID id) {
        CivilServantType entity = null;
        Optional<CivilServantType> civil = civilServantTypeRepository.findById(id);
        if (civil.isPresent()) {
            entity = civil.get();
        }
        if (entity != null) {
            return new CivilServantTypeDto(entity);
        }
        return null;
    }

    @Override
    public CivilServantTypeDto saveOrUpdate(CivilServantTypeDto dto, UUID id) {
        if (dto != null) {
            LocalDateTime currentDate = LocalDateTime.now();
            String currentUserName = "Unknown User";

            CivilServantType civilServantType = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<CivilServantType> civil = civilServantTypeRepository.findById(id);
                if (civil.isPresent()) {
                    civilServantType = civil.get();
                }
            }

            if (civilServantType == null) {
                civilServantType = new CivilServantType();
                civilServantType.setCreateDate(currentDate);
                civilServantType.setModifyDate(currentDate);
                civilServantType.setCreatedBy(currentUserName);
                civilServantType.setModifiedBy(currentUserName);
            } else {
                civilServantType.setModifyDate(currentDate);
                civilServantType.setModifiedBy(currentUserName);
            }
            civilServantType.setCode(dto.getCode());
            civilServantType.setName(dto.getName());
            civilServantType.setLanguageKey(dto.getLanguageKey());

            civilServantType = civilServantTypeRepository.save(civilServantType);
            return new CivilServantTypeDto(civilServantType);
        }
        return null;
    }

    @Override
    public Page<CivilServantTypeDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from  CivilServantType as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.CivilServantTypeDto(entity) from  CivilServantType as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, CivilServantTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<CivilServantTypeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public void deleteOne(UUID id) {
        CivilServantType civilServantType = this.findById(id);
        if (civilServantType != null) {
            civilServantTypeRepository.delete(civilServantType);
        }

    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        List<CivilServantType> entities = civilServantTypeRepository.findByCode(code);
        if (entities != null && entities.size() > 0 && entities.get(0) != null && entities.get(0).getId() != null) {
            if (id != null && StringUtils.hasText(id.toString())) {
                return !entities.get(0).getId().equals(id);
            }
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkName(UUID id, String name) {
        List<CivilServantType> entities = civilServantTypeRepository.findByName(name);
        if (entities != null && entities.size() > 0 && entities.get(0) != null && entities.get(0).getId() != null) {
            if (id != null) {
                return !entities.get(0).getId().equals(id);
            }
            return true;
        }
        return false;
    }

    @Override
    public CivilServantTypeDto findByCode(String code) {
        if (code == null || code.length() == 0) return null;
        List<CivilServantType> entities = civilServantTypeRepository.findByCode(code);
        if (entities != null && entities.size() > 0) {
            return new CivilServantTypeDto(entities.get(0));
        }
        return null;
    }
}
