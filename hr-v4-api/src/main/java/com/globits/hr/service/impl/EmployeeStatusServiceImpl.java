package com.globits.hr.service.impl;

import com.globits.hr.domain.EmployeeStatus;
import com.globits.hr.dto.EmployeeStatusDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.EmployeeStatusRepository;
import com.globits.hr.service.EmployeeStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeStatusServiceImpl implements EmployeeStatusService {
    @PersistenceContext
    EntityManager manager;

    @Autowired
    EmployeeStatusRepository employeeStatusRepository;

    @Override
    public Page<EmployeeStatusDto> searchByPage(SearchDto dto) {
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

        String orderBy = " ORDER BY entity.id DESC";

        String sqlCount = "select count(entity.id) from EmployeeStatus as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.EmployeeStatusDto(entity) from EmployeeStatus as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.code) LIKE UPPER(:text) ) OR ( UPPER(entity.name) LIKE UPPER(:text) )";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, EmployeeStatusDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<EmployeeStatusDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public EmployeeStatusDto saveOrUpdate(EmployeeStatusDto dto) {
        UUID id = dto.getId();
        EmployeeStatus entity = null;
        if (id != null) {
            Optional<EmployeeStatus> optional = employeeStatusRepository.findById(dto.getId());
            if (optional.isPresent()) {
                entity = optional.get();
            }
        }
        if (entity == null) {
            entity = new EmployeeStatus();
        }
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setLanguageKey(dto.getLanguageKey());
        entity.setActive(dto.isActive());

        entity = employeeStatusRepository.save(entity);
        return new EmployeeStatusDto(entity);
    }

    @Override
    public EmployeeStatusDto getOne(UUID id) {
        EmployeeStatus entity = null;
        Optional<EmployeeStatus> optional = employeeStatusRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new EmployeeStatusDto(entity);
        }
        return null;
    }

    @Override
    public void deleteOne(UUID id) {
        EmployeeStatus entity = null;
        Optional<EmployeeStatus> optional = employeeStatusRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            employeeStatusRepository.delete(entity);
        }
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        List<EmployeeStatus> entities = employeeStatusRepository.findByCode(code);
        if (entities != null && entities.size() > 0 && entities.get(0) != null && entities.get(0).getId() != null) {
            if (id != null && StringUtils.hasText(id.toString())) {
                return !entities.get(0).getId().equals(id);
            }
            return true;
        }
        return false;
    }
}
