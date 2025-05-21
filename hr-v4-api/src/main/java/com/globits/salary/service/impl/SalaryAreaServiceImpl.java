package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryArea;
import com.globits.salary.domain.SalaryUnit;
import com.globits.salary.dto.SalaryAreaDto;
import com.globits.salary.dto.SalaryUnitDto;
import com.globits.salary.repository.SalaryAreaRepository;
import com.globits.salary.service.SalaryAreaService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class SalaryAreaServiceImpl extends GenericServiceImpl<SalaryArea, UUID> implements SalaryAreaService {

    @Autowired
    private SalaryAreaRepository salaryAreaRepository;

    @Override
    public SalaryAreaDto saveOrUpdateSalaryArea(SalaryAreaDto dto) {
        if (dto == null || dto.getCode() == null || dto.getName() == null) return null;

        SalaryArea entity = null;
        if (dto.getId() != null) {
            entity = salaryAreaRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        }
        if (entity == null) entity = new SalaryArea();

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setMinHour(dto.getMinHour());
        entity.setMinMonth(dto.getMinMonth());

        entity = salaryAreaRepository.save(entity);

        return new SalaryAreaDto(entity);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (code == null) return false;
        if (id == null) {
            List<SalaryArea> entities = salaryAreaRepository.findByCode(code.strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<SalaryArea> entities = salaryAreaRepository.findByCode(code.strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (SalaryArea entity : entities) {
                if (!entity.getId().equals(id)) return false;
            }
        }
        return true;
    }

    @Override
    public Boolean deleteSalaryAres(UUID id) {
        try {
            Optional<SalaryArea> salaryAreaOptional = salaryAreaRepository.findById(id);
            if (salaryAreaOptional.isPresent()) {
                salaryAreaRepository.deleteById(id);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error deleting SalaryArea: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Boolean deleteMultipleSalaryArea(List<UUID> listIds) {
        if (listIds == null || listIds.size() < 1) return null;
        boolean isValid = true;
        for (UUID id : listIds) {
            boolean statusDelete = this.deleteSalaryAres(id);
            if (!statusDelete) isValid = false;

        }
        return isValid;
    }

    @Override
    public SalaryAreaDto getSalaryAreaById(UUID id) {
        SalaryArea salaryArea = this.findById(id);
        if (salaryArea != null) {
            return new SalaryAreaDto(salaryArea);
        }
        return null;
    }

    @Override
    public Page<SalaryAreaDto> searchByPage(SearchDto dto) {
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

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(distinct entity.id) from SalaryArea as entity ";
        String sql = "select distinct new com.globits.salary.dto.SalaryAreaDto(entity) from SalaryArea as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, SalaryAreaDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        List<SalaryAreaDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<SalaryAreaDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;

    }


}
