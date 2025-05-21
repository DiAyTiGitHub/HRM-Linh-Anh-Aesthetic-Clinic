package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.PositionRole;
import com.globits.hr.dto.PositionRoleDto;
import com.globits.hr.dto.search.SearchPositionRole;
import com.globits.hr.repository.PositionRoleRepository;
import com.globits.hr.service.PositionRoleService;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
@Service
public class PositionRoleServiceImpl extends GenericServiceImpl<PositionRole, UUID> implements PositionRoleService {

    @Resource
    private PositionRoleRepository positionRoleRepository;

    @Override
    @Modifying
    @Transactional
    public PositionRoleDto savePositionRole(PositionRoleDto dto) {
        if(dto == null) return null;
        PositionRole positionRole = new PositionRole();
        if(dto.getId()!=null) {
            positionRole = positionRoleRepository.findById(dto.getId()).orElse(null);;
        }
        if(positionRole == null){
            positionRole = new PositionRole();
        }
        positionRole.setName(dto.getName());
        positionRole.setOtherName(dto.getOtherName());
        positionRole.setShortName(dto.getShortName());
        positionRole.setDescription(dto.getDescription());
        positionRole=positionRoleRepository.save(positionRole);
        return new PositionRoleDto(positionRole);
    }

    @Override
    public PositionRoleDto getById(UUID id) {
         PositionRole positionRole = positionRoleRepository.findById(id).orElse(null);
         if(positionRole != null) {
             return new PositionRoleDto(positionRole);
         };
         return null;
    }
    @Override
    @Modifying
    @Transactional
    public  Boolean deletePositionRole(UUID id) {
        if(id == null) return false;
        PositionRole positionRole = positionRoleRepository.findById(id).orElse(null);
        if(positionRole == null) return false;
        positionRoleRepository.delete(positionRole);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultiplePositionRoles(List<UUID> ids) {
        if(ids == null) return false;
        boolean isValid = true;
        for(UUID positionRoleId : ids) {
            boolean deleteRes = this.deletePositionRole(positionRoleId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<PositionRoleDto> pagingPositionRoles(SearchPositionRole dto) {
        if (dto == null) {
            return Page.empty();
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

        String sqlCount = "select count(distinct entity.id) from PositionRole as entity ";
        String sql = "select distinct new com.globits.hr.dto.PositionRoleDto(entity) from PositionRole as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.shortName LIKE :text OR entity.otherName LIKE :text) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, PositionRoleDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<PositionRoleDto> entities = query.getResultList();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public PositionRoleDto findByShortName(String shortName) {
        if (shortName == null || shortName.isEmpty()) return null;
        List<PositionRole> entities = positionRoleRepository.findByShortName(shortName);
        if (entities != null && !entities.isEmpty()) {
            return new PositionRoleDto(entities.get(0));
        }
        return null;
    }

}
