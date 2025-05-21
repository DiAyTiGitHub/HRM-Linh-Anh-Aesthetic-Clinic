package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.DepartmentGroup;
import com.globits.hr.dto.DepartmentGroupDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.DepartmentGroupRepository;
import com.globits.hr.service.DepartmentGroupService;
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

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentGroupServiceImpl extends GenericServiceImpl<DepartmentGroup, UUID> implements DepartmentGroupService {
    @Autowired
    DepartmentGroupRepository departmentGroupRepository;

    @Override
    public DepartmentGroupDto saveOrUpdate(DepartmentGroupDto dto) {
        if (dto == null) return null;
        DepartmentGroup newDepartmentGroup = null;
        if (dto.getId() != null) {
            newDepartmentGroup = departmentGroupRepository.findById(dto.getId()).orElse(null);
            if (newDepartmentGroup == null) return null;
        }
        if (newDepartmentGroup == null) {
            newDepartmentGroup = new DepartmentGroup();
        }
        if (dto.getId() == null) {
            newDepartmentGroup = new DepartmentGroup();
        }
        if (dto.getName() != null) {
            newDepartmentGroup.setName(dto.getName());
        }
        if (dto.getShortName() != null) {
            newDepartmentGroup.setShortName(dto.getShortName());
        }
        if (dto.getOtherName() != null) {
            newDepartmentGroup.setOtherName(dto.getOtherName());
        }
        if (dto.getDescription() != null) {
            newDepartmentGroup.setDescription(dto.getDescription());
        }
        newDepartmentGroup.setSortNumber(dto.getSortNumber());

        return new DepartmentGroupDto(departmentGroupRepository.save(newDepartmentGroup));
    }

    @Override
    public Page<DepartmentGroupDto> pageBySearch(SearchDto dto) {
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
        String sql = "Select distinct new com.globits.hr.dto.DepartmentGroupDto(entity) from DepartmentGroup entity";
        String sqlCount = ("Select count(distinct entity) from DepartmentGroup entity");
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereCaule += " And(entity.name like :name)";
        }
        sql += whereCaule + orderBy;
        sqlCount += whereCaule;
        int startPosition = pageIndex * pageSize;
        Query query = manager.createQuery(sql, DepartmentGroupDto.class);
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Query queryCount = manager.createQuery(sqlCount);
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("name", "%" + dto.getKeyword() + "%");
            queryCount.setParameter("name", "%" + dto.getKeyword() + "%");
        }
        Page<DepartmentGroupDto> result;
        List<DepartmentGroupDto> listDepartmentGroup = query.getResultList();
        long count = (long) queryCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        result = new PageImpl(listDepartmentGroup, pageable, count);
        return result;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteDepartmentGroup(UUID id) {
        if (id == null) return null;
        DepartmentGroup DepartmentGroup = departmentGroupRepository.findById(id).orElse(null);
        if (DepartmentGroup == null) return false;
        departmentGroupRepository.delete(DepartmentGroup);
        return true;
    }

    @Override
    public DepartmentGroupDto getById(UUID id) {
        if (id == null) return null;
        DepartmentGroup DepartmentGroup = departmentGroupRepository.findById(id).orElse(null);
        if (DepartmentGroup == null) return null;
        return new DepartmentGroupDto(DepartmentGroup);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultiple(List<UUID> listIds) {
        if (listIds == null || listIds.isEmpty()) return false;
        boolean isValid = true;
        for (UUID id : listIds) {
            boolean status = this.deleteDepartmentGroup(id);
            if (!status) isValid = false;
        }
        return isValid;
    }

    @Override
    public DepartmentGroupDto findByShortName(String shortName) {
        if (shortName == null || shortName.isEmpty()) return null;
        List<DepartmentGroup> entities = departmentGroupRepository.findByShortName(shortName);
        if (entities != null && !entities.isEmpty()) {
            return new DepartmentGroupDto(entities.get(0));
        }
        return null;
    }

}
