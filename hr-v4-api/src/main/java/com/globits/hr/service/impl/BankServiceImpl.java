package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Bank;
import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.BankRepository;
import com.globits.hr.service.BankService;
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
public class BankServiceImpl extends GenericServiceImpl<Bank, UUID> implements BankService {
    @Autowired
    private BankRepository bankRepository;

    @Override
    public BankDto getById(UUID id) {
        if (id != null) {
            Bank entity = bankRepository.findById(id).orElse(null);
            if (entity != null) {
                return new BankDto(entity);
            }
        }
        return null;
    }

    @Override
    public Boolean checkCode(BankDto dto) {
        if (dto.getCode() == null) {
            return false;
        }

        List<Bank> entities = bankRepository.findByCode(dto.getCode());
        if (entities.isEmpty()) {
            return true;
        }
        for (Bank entity : entities) {
            if (entity.getId().equals(dto.getId())) {
                return true;
            }

        }
        return false;
    }

    @Override
    public BankDto saveOrUpdate(BankDto dto) {
        if (dto != null) {
            Bank entity = null;
            if (dto.getId() != null) {
                entity = bankRepository.findById(dto.getId()).orElse(null);
            }
            if (entity == null) {
                entity = new Bank();
            }
            entity.setName(dto.getName());
            entity.setCode(dto.getCode());
            entity.setDescription(dto.getDescription());
            Bank response = bankRepository.save(entity);
            return new BankDto(response);
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            Bank entity = bankRepository.findById(id).orElse(null);
            if (entity != null) {
                bankRepository.delete(entity);
                return true;
            }
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
    public Page<BankDto> paging(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from Bank as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.BankDto(entity) from Bank as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
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
        List<BankDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

}
