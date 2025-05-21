package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.AllowanceType;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.dto.AllowanceTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.AllowanceTypeRepository;
import com.globits.hr.service.AllowanceTypeService;

import com.globits.salary.domain.SalaryUnit;
import com.globits.salary.repository.SalaryUnitRepository;
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
public class AllowanceTypeServiceImpl extends GenericServiceImpl<AllowanceType, UUID> implements AllowanceTypeService {
    
	@Autowired
    private AllowanceTypeRepository allowanceTypeRepository;

    @Autowired
    private SalaryUnitRepository salaryUnitRepository;

    @Override
    public AllowanceTypeDto saveOrUpdate(AllowanceTypeDto dto, UUID id) {
        if (dto == null) return null;

        AllowanceType entity = null;
        if (id != null) {
            entity = allowanceTypeRepository.findById(id).orElse(null);
        }
        if (entity == null && dto.getId() != null) {
            entity = allowanceTypeRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        }
        if (entity == null) entity = new AllowanceType();

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDefaultValue(dto.getDefaultValue());
        entity.setInsuranceValue(dto.getInsuranceValue());
        entity.setOtherName(dto.getOtherName());
        entity.setTaxReductionValue(dto.getTaxReductionValue());

        if (dto.getTaxReductionValueUnit() != null && dto.getTaxReductionValueUnit().getId() != null) {
            SalaryUnit taxReductionUnit = salaryUnitRepository
                    .findById(dto.getTaxReductionValueUnit().getId()).orElse(null);
            if (taxReductionUnit == null) return null;
            entity.setTaxReductionValueUnit(taxReductionUnit);
        } else {
            entity.setTaxReductionValueUnit(null);
        }

        if (dto.getDefaultValueUnit() != null && dto.getDefaultValueUnit().getId() != null) {
            SalaryUnit defaultValueUnit = salaryUnitRepository
                    .findById(dto.getDefaultValueUnit().getId()).orElse(null);
            if (defaultValueUnit == null) return null;
            entity.setDefaultValueUnit(defaultValueUnit);
        } else {
            entity.setDefaultValueUnit(null);
        }

        if (dto.getInsuranceValueUnit() != null && dto.getInsuranceValueUnit().getId() != null) {
            SalaryUnit insuranceValueUnit = salaryUnitRepository
                    .findById(dto.getDefaultValueUnit().getId()).orElse(null);
            if (insuranceValueUnit == null) return null;
            entity.setInsuranceValueUnit(insuranceValueUnit);
        } else {
            entity.setInsuranceValueUnit(null);
        }

        entity = allowanceTypeRepository.save(entity);
        if (entity == null) return null;
        AllowanceTypeDto response = new AllowanceTypeDto(entity);

        return response;
    }

    @Override
    public void remove(UUID id) {
        AllowanceType entity = null;
        Optional<AllowanceType> optional = allowanceTypeRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            allowanceTypeRepository.delete(entity);
        }
    }

    @Override
    public AllowanceTypeDto getAllowanceType(UUID id) {
        AllowanceType entity = null;
        Optional<AllowanceType> optional = allowanceTypeRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new AllowanceTypeDto(entity);
        }
        return null;
    }

    @Override
    public Page<AllowanceTypeDto> searchByPage(SearchDto dto) {
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
        String sqlCount = "select count(entity.id) from AllowanceType as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.AllowanceTypeDto(entity) from AllowanceType as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) ) OR ( UPPER(entity.otherName) LIKE UPPER(:text) )";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, AllowanceTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<AllowanceTypeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (code == null) return false;
        if (id == null) {
            List<AllowanceType> entities = allowanceTypeRepository.findByCode(code.strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<AllowanceType> entities = allowanceTypeRepository.findByCode(code.strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (AllowanceType entity : entities) {
                if (!entity.getId().equals(id)) return false;
            }
        }
        return true;
    }
    
}
