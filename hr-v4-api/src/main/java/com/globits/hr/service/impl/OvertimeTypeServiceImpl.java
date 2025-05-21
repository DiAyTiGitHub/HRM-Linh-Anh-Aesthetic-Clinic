/*
 * Created by TA & Giang on 22/4/2018.
 */

package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.OvertimeType;
import com.globits.hr.dto.OvertimeTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.OvertimeTypeRepository;
import com.globits.hr.service.OvertimeTypeService;
import com.globits.security.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class OvertimeTypeServiceImpl extends GenericServiceImpl<OvertimeType, UUID> implements OvertimeTypeService {

    @Autowired
    OvertimeTypeRepository overtimeTypeRepository;

    @Override
    public OvertimeTypeDto saveOvertimeType(OvertimeTypeDto dto) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        User modifiedUser;
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        if (authentication != null) {
            modifiedUser = (User) authentication.getPrincipal();
            currentUserName = modifiedUser.getUsername();
        }

        OvertimeType overtimeType = null;
        if (dto != null) {
            if (dto.getId() != null) {
                Optional<OvertimeType> optional = overtimeTypeRepository.findById(dto.getId());
                if (optional.isPresent()) {
                    overtimeType = optional.get();
                }
            }
            if (overtimeType == null) {
                overtimeType = new OvertimeType();
                overtimeType.setCreateDate(currentDate);
                overtimeType.setCreatedBy(currentUserName);
            }
            if (dto.getCode() != null) {
                overtimeType.setCode(dto.getCode());
            }
            overtimeType.setName(dto.getName());
            overtimeType.setModifyDate(currentDate);
            overtimeType.setModifiedBy(currentUserName);
            overtimeType = overtimeTypeRepository.save(overtimeType);
            return new OvertimeTypeDto(overtimeType);
        }
        return null;
    }

    @Override
    public Boolean deleteOvertimeType(UUID id) {
        OvertimeType overtimeType = null;
        Optional<OvertimeType> optional = overtimeTypeRepository.findById(id);
        if (optional.isPresent()) {
            overtimeType = optional.get();
        }
        if (overtimeType != null) {
            overtimeTypeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public OvertimeTypeDto getOvertimeType(UUID id) {
        OvertimeType overtimeType = null;
        Optional<OvertimeType> optional = overtimeTypeRepository.findById(id);
        if (optional.isPresent()) {
            overtimeType = optional.get();
        }
        if (overtimeType != null) {
            return new OvertimeTypeDto(overtimeType);
        }
        return null;
    }

    @Override
    public Page<OvertimeTypeDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from OvertimeType as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.OvertimeTypeDto(entity) from OvertimeType as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, OvertimeTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<OvertimeTypeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = overtimeTypeRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }


}
