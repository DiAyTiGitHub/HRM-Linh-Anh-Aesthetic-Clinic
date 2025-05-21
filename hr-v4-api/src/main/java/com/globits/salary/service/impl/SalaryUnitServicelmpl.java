package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.CivilServantGrade;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.dto.CivilServantGradeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryUnit;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryUnitDto;
import com.globits.salary.repository.SalaryUnitRepository;
import com.globits.salary.service.SalaryUnitService;
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
public class SalaryUnitServicelmpl extends GenericServiceImpl<SalaryUnit, UUID> implements SalaryUnitService {
    @Autowired
    SalaryUnitRepository salaryUnitRepository;

    @Override
    public SalaryUnitDto saveOrUpdate(SalaryUnitDto dto) {
        if (dto == null || dto.getCode() == null || dto.getName() == null) return null;

        SalaryUnit entity = null;
        if (dto.getId() != null) {
            entity = salaryUnitRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        }
        if (entity == null) entity = new SalaryUnit();

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setManDays(dto.getManDays());

        entity = salaryUnitRepository.save(entity);
        if (entity == null) return null;

        return new SalaryUnitDto(entity);
    }

    @Override
    public Boolean deleteSalaryUnit(UUID id) {
        try {
            Optional<SalaryUnit> salaryUnitOptional = salaryUnitRepository.findById(id);
            if (salaryUnitOptional.isPresent()) {
                salaryUnitRepository.deleteById(id);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error deleting SalaryUnit: " + e.getMessage());
        }
        return false;
    }


    @Override
    public Boolean deleteMultipleSalaryUnit(List<UUID> listIds) {
        if (listIds == null || listIds.size() < 1) return null;

        boolean isValid = true;
        for (UUID id : listIds) {
            boolean statusDelete = this.deleteSalaryUnit(id);
            if (!statusDelete) isValid = false;

        }
        return isValid;
    }


    @Override
    public SalaryUnitDto getById(UUID id) {
        SalaryUnit salaryUnit = this.findById(id);
        if (salaryUnit != null) {
            return new SalaryUnitDto(salaryUnit);
        }
        return null;
    }


    @Override
    public Boolean checkCode(UUID id, String code) {
        if (code == null) return false;
        if (id == null) {
            List<SalaryUnit> entities = salaryUnitRepository.findByCode(code.strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<SalaryUnit> entities = salaryUnitRepository.findByCode(code.strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (SalaryUnit entity : entities) {
                if (!entity.getId().equals(id)) return false;
            }
        }
        return true;
    }

    @Override
    public Page<SalaryUnitDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(distinct entity.id) from SalaryUnit as entity ";
        String sql = "select distinct new com.globits.salary.dto.SalaryUnitDto(entity) from SalaryUnit as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, SalaryUnitDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        List<SalaryUnitDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<SalaryUnitDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

}
