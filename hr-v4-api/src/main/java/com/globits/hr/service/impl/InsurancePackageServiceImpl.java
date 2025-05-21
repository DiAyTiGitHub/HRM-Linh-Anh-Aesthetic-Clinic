package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.InsurancePackage;
import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.InsurancePackageDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.InsurancePackageRepository;
import com.globits.hr.service.InsurancePackageItemService;
import com.globits.hr.service.InsurancePackageService;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class InsurancePackageServiceImpl extends GenericServiceImpl<InsurancePackage, UUID> implements InsurancePackageService {
    @Autowired
    private InsurancePackageRepository insurancePackageRepository;

    @Autowired
    private InsurancePackageItemService insurancePackageItemService;

    @Override
    public InsurancePackageDto getById(UUID id) {
        if (id == null) {
            return null;
        }

        InsurancePackage entity = insurancePackageRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }

        return new InsurancePackageDto(entity, true);
    }

    @Override
    public Boolean isValidCode(InsurancePackageDto dto) {
        if (dto == null)
            return false;

        // ID of InsurancePackage is null => Create new InsurancePackage
        // => Assure that there's no other InsurancePackages using this code of new InsurancePackage
        // if there was any InsurancePackage using new InsurancePackage code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<InsurancePackage> entities = insurancePackageRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        }
        // ID of InsurancePackage is NOT null => InsurancePackage is modified
        // => Assure that the modified code is not same to OTHER any InsurancePackage's code
        // if there was any InsurancePackage using new InsurancePackage code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<InsurancePackage> entities = insurancePackageRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (InsurancePackage entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public InsurancePackageDto saveOrUpdate(InsurancePackageDto dto) {
        if (dto == null) {
            return null;
        }

        InsurancePackage entity = null;
        if (dto.getId() != null) {
            entity = insurancePackageRepository.findById(dto.getId()).orElse(null);
        }

        if (entity == null) {
            entity = new InsurancePackage();
        }

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());

        insurancePackageItemService.handleSetItemsForInsurancePackage(entity, dto);

        InsurancePackage response = insurancePackageRepository.saveAndFlush(entity);

        return new InsurancePackageDto(response);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) {
            return false;
        }

        InsurancePackage entity = insurancePackageRepository.findById(id).orElse(null);

        if (entity != null) {
            insurancePackageRepository.delete(entity);
            return true;
        }

        return false;

    }

    @Override
    @Transactional
    public Integer deleteMultiple(List<UUID> ids) {
        int result = 0;
        for (UUID id : ids) {
            deleteById(id);
            result++;
        }
        return result;
    }

    @Override
    public Page<InsurancePackageDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from InsurancePackage as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.InsurancePackageDto(entity) from InsurancePackage as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, BankDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);

        List<InsurancePackageDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

}
