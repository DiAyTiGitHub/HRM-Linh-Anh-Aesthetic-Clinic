package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.DepartmentType;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.DepartmentTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.DepartmentTypeRepository;
import com.globits.hr.service.DepartmentTypeService;
import com.globits.hr.service.SystemConfigService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DepartmentTypeServiceImpl extends GenericServiceImpl<DepartmentType, UUID>
        implements DepartmentTypeService {
    @Autowired
    DepartmentTypeRepository departmentTypeRepository;

    @Autowired
    SystemConfigService systemConfigService;

    @Override
    public DepartmentTypeDto saveOrUpdate(DepartmentTypeDto dto) {
        if (dto == null) return null;
        DepartmentType newDepartmentType = null;
        if (dto.getId() != null) {
            newDepartmentType = departmentTypeRepository.findById(dto.getId()).orElse(null);
            if (newDepartmentType == null) return null;
        }
        if (newDepartmentType == null) {
            newDepartmentType = new DepartmentType();
        }
        if (dto.getId() == null) {
            newDepartmentType = new DepartmentType();
        }
        if(dto.getName()!=null){
            newDepartmentType.setName(dto.getName());
        }
        if(dto.getCode()!=null){
            newDepartmentType.setCode(dto.getCode());
        }
        if (dto.getShortName() != null) {
            newDepartmentType.setShortName(dto.getShortName());
        }
        if (dto.getOtherName() != null) {
            newDepartmentType.setOtherName(dto.getOtherName());
        }
        if (dto.getDescription() != null) {
            newDepartmentType.setDescription(dto.getDescription());
        }
        newDepartmentType.setSortNumber(dto.getSortNumber());

        return new DepartmentTypeDto(departmentTypeRepository.save(newDepartmentType));
    }

    @Override
    public Page<DepartmentTypeDto> pageBySearch(SearchDto dto) {
        if (dto == null) return null;
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String whereCaule = " Where(1=1)";
        String orderBy = " ORDER BY entity.createDate ";
        String sql = "Select distinct new com.globits.hr.dto.DepartmentTypeDto(entity) from DepartmentType entity";
        String sqlCount = ("Select count(distinct entity) from DepartmentType entity");
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereCaule += " AND (entity.name LIKE :keyword OR entity.code LIKE :keyword OR entity.shortName LIKE :keyword OR entity.otherName LIKE :keyword OR entity.description LIKE :keyword)";
        }
        sql += whereCaule +orderBy;
        sqlCount += whereCaule;
        int startPosition = pageIndex * pageSize;
        Query query = manager.createQuery(sql, DepartmentTypeDto.class);
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Query queryCount = manager.createQuery(sqlCount);
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("keyword", "%" + dto.getKeyword() + "%");
            queryCount.setParameter("keyword", "%" + dto.getKeyword() + "%");
        }
        Page<DepartmentTypeDto> result;
        List<DepartmentTypeDto> listDepartmentType = query.getResultList();
        long count = (long) queryCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        result = new PageImpl(listDepartmentType, pageable, count);
        return result;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteDepartmentType(UUID id) {
        if (id == null) return null;
        DepartmentType departmentType = departmentTypeRepository.findById(id).orElse(null);
        if (departmentType == null) return false;
        departmentTypeRepository.delete(departmentType);
        return true;
    }

    @Override
    public DepartmentTypeDto getById(UUID id) {
        if (id == null) return null;
        DepartmentType departmentType = departmentTypeRepository.findById(id).orElse(null);
        if (departmentType == null) return null;
        return new DepartmentTypeDto(departmentType);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultiple(List<UUID> listIds) {
        if (listIds == null || listIds.isEmpty()) return false;
        boolean isValid = true;
        for (UUID id : listIds) {
            boolean status = this.deleteDepartmentType(id);
            if (!status) isValid = false;
        }
        return isValid;
    }

    @Override
    public DepartmentTypeDto findByShortName(String shortName) {
        if (shortName == null || shortName.isEmpty()) return null;
        List<DepartmentType> entities = departmentTypeRepository.findByShortName(shortName);
        if (entities != null && !entities.isEmpty()) {
            return new DepartmentTypeDto(entities.get(0));
        }
        return null;
    }

	@Override
	public Integer saveListDepartmentType(List<DepartmentTypeDto> listData) {
	    if (listData == null || listData.isEmpty()) return 0;
	    
	    List<DepartmentType> entitiesToSave = new ArrayList<>(listData.size());

	    for (DepartmentTypeDto dto : listData) {
	        if (dto == null
	                || dto.getName() == null
	                || dto.getCode() == null
	                || dto.getSortNumber() == null) {
	            continue;
	        }
	        DepartmentType entity = null;
	        if (dto.getCode() != null && !dto.getCode().isEmpty()) {
	        	List<DepartmentType> listEntity = departmentTypeRepository.findByCode(dto.getCode());
	        	if (listEntity != null && listEntity.size() > 0) {
	        		entity = listEntity.get(0);
	        	}
	        }
	        if (entity == null) {
	        	entity = new DepartmentType();
	        }
	        entity.setName(dto.getName());
	        entity.setCode(dto.getCode());
	        entity.setSortNumber(dto.getSortNumber());
	        entity.setOtherName(dto.getOtherName());
	        entity.setShortName(dto.getShortName());
	        entitiesToSave.add(entity);
	    }
	    if (entitiesToSave.isEmpty()) {
	        return 0;
	    }
	    // Save batch
	    try {
	        departmentTypeRepository.saveAll(entitiesToSave);
	    } catch (Exception e) {
	        System.err.println("Lỗi khi lưu danh sách loại phòng ban: " + e.getMessage());
	        return 0;
	    }

	    return entitiesToSave.size();
	}

    @Override
    public Boolean isValidCode(DepartmentTypeDto dto) {
        if (dto == null)
            return false;
        if (dto.getId() == null) {
            List<DepartmentType> entities = departmentTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<DepartmentType> entities = departmentTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (DepartmentType entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = departmentTypeRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }


}
