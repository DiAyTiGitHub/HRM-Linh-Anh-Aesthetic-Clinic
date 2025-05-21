package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryType;
import com.globits.salary.dto.SalaryTypeDto;
import com.globits.salary.dto.SalaryUnitDto;
import com.globits.salary.repository.SalaryTypeRepository;
import com.globits.salary.service.SalaryTypeService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Transactional
@Service
public class SalaryTypeServicelmpl extends GenericServiceImpl<SalaryType, UUID> implements SalaryTypeService {

    @Autowired
    SalaryTypeRepository salaryTypeRepository;

    @Override
    public SalaryTypeDto saveSalaryType(SalaryTypeDto dto) {
        if(dto== null || dto.getName()==null) return null;
        SalaryType entity = null;
        if(dto.getId()!=null) {
            entity = salaryTypeRepository.findById(dto.getId()).orElse(null);
            if(entity==null) return null;
        }
        if(entity==null) entity = new SalaryType();

        List<SalaryType> existedDupName = salaryTypeRepository.findByName(dto.getName());
        boolean isDup = false;

        for (SalaryType existedItem : existedDupName){
            if(existedItem.getId() != entity.getId()){
                isDup = true;
                break;
            }
        }
        if(isDup) return null;
        entity.setName(dto.getName());
        entity.setOtherName(dto.getOtherName());
        entity.setDescription(dto.getDescription());
        entity = salaryTypeRepository.save(entity);
        if(entity==null) return null;
        return new SalaryTypeDto(entity);

    }

    @Override
    public  SalaryTypeDto getById(UUID id) {
        SalaryType entity = salaryTypeRepository.findById(id).orElse(null);
        if(entity!=null) {
            return new SalaryTypeDto(entity);
        };
        return null;
    }

    @Override
    public  Boolean deleteSalaryType(UUID id) {
        try{
            Optional<SalaryType> salaryTypeOptional = salaryTypeRepository.findById(id);
            if(salaryTypeOptional.isPresent()) {
                salaryTypeRepository.deleteById(id);
                return true;
            }
        }catch (Exception e) {
            System.err.println("Error deleting SalaryUnit: " + e.getMessage());
        }
        return false;
    }

    @Override
    public  Boolean deleteMultipleSalaryTypes(List<UUID> listIds) {
        if (listIds == null || listIds.size() < 1) return null;

        boolean isValid = true;
        for (UUID id : listIds) {
            boolean status = this.deleteSalaryType(id);
            if (!status) isValid=false;
        }
        return isValid;

    }

    @Override
    public Page<SalaryTypeDto> pagingSalaryTypes(SearchDto dto) {
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

        String sqlCount = "select count(distinct entity.id) from SalaryType as entity ";
        String sql = "select distinct new com.globits.salary.dto.SalaryTypeDto(entity) from SalaryType as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.otherName LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, SalaryTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        List<SalaryTypeDto> entities = new ArrayList<>();

        long count = (long) qCount.getSingleResult();
        Page<SalaryTypeDto> result;
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;

    }
}
