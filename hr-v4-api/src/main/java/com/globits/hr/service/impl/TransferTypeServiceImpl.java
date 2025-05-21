package com.globits.hr.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.LeavingJobReason;
import com.globits.hr.domain.TransferType;
import com.globits.hr.dto.TransferTypeDto;
import com.globits.hr.repository.TransferTypeRepository;
import com.globits.hr.service.TransferTypeService;
import jakarta.annotation.Resource;
import jakarta.persistence.Query;
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
public class TransferTypeServiceImpl extends GenericServiceImpl<TransferType, UUID> implements TransferTypeService {

    @Resource
    private TransferTypeRepository transferTypeRepository;

    @Override
    @Modifying
    @Transactional
    public TransferTypeDto saveOrUpdate(TransferTypeDto dto) {
        if (dto == null) {
            return null;
        }

        TransferType transferType = new TransferType();
        if (dto.getId() != null) transferType = transferTypeRepository.findById(dto.getId()).orElse(null);
        if (transferType == null) transferType = new TransferType();

        transferType.setName(dto.getName());
        transferType.setCode(dto.getCode());
        transferType.setDescription(dto.getDescription());

        transferType = transferTypeRepository.save(transferType);

        return new TransferTypeDto(transferType);
    }

    @Override
    public Page<TransferTypeDto> searchByPage(SearchDto searchDto) {
        if (searchDto == null) {
            return null;
        }

        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(distinct entity.id) from TransferType as entity ";
        String sql = "select distinct new com.globits.hr.dto.TransferTypeDto(entity) from TransferType as entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, TransferTypeDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }

        List<TransferTypeDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<TransferTypeDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public TransferTypeDto getById(UUID id) {
        TransferType optionalTransferType = transferTypeRepository.findById(id).orElse(null);
        if (optionalTransferType != null) {
            return new TransferTypeDto(optionalTransferType);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean remove(UUID id) {
        if (id == null) return false;

        TransferType transferType = transferTypeRepository.findById(id).orElse(null);
        if (transferType == null) return false;

        transferTypeRepository.delete(transferType);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean removeMultiple(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.remove(id);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public TransferTypeDto findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        List<TransferType> entities = transferTypeRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new TransferTypeDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean checkCode(TransferTypeDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<TransferType> entities = transferTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<TransferType> entities = transferTypeRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (TransferType entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;    }

}
