package com.globits.budget.service.impl;


import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.budget.domain.BudgetCategory;
import com.globits.budget.dto.BudgetCategoryDto;
import com.globits.budget.dto.budget.BudgetSearchDto;
import com.globits.budget.repository.BudgetCategoryRepository;
import com.globits.budget.service.BudgetCategoryService;
import com.globits.hr.domain.LeavingJobReason;
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
public class BudgetCategoryServiceImpl extends GenericServiceImpl<BudgetCategory, UUID> implements BudgetCategoryService {
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;

    @Override
    public Page<BudgetCategoryDto> pagingBudgetCategory(BudgetSearchDto dto) {
        if (dto == null)
            return null;

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0)
            pageIndex--;
        else
            pageIndex = 0;


        String sqlCount = "select count(entity.id) from BudgetCategory entity where (1=1)";
        String sql = "select new com.globits.budget.dto.BudgetCategoryDto(entity) from BudgetCategory as entity where (1=1)";

        String whereClause = "";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text) ";
        }

        String orderBy = "ORDER BY entity.createDate DESC";


        sql += whereClause + orderBy;
        sqlCount += whereClause;
        Query q = manager.createQuery(sql, BudgetCategoryDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<BudgetCategoryDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public BudgetCategoryDto saveOrUpdate(BudgetCategoryDto dto) {
        if (dto != null) {
            BudgetCategory entity = null;
            if (dto.getId() != null) {
                entity = budgetCategoryRepository.findById(dto.getId()).orElse(null);
            }
            if (entity == null) {
                entity = new BudgetCategory();
            }
            entity.setName(dto.getName());
            entity.setCode(dto.getCode());
            entity.setDescription(dto.getDescription());
            entity.setIcon(dto.getIcon());

            BudgetCategory response = budgetCategoryRepository.save(entity);
            return new BudgetCategoryDto(response);
        }

        return null;
    }

    @Override
    public BudgetCategoryDto getById(UUID id) {
        if (id != null) {
            BudgetCategory entity = budgetCategoryRepository.findById(id).orElse(null);
            if (entity != null) {
                return new BudgetCategoryDto(entity);
            }
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            BudgetCategory entity = budgetCategoryRepository.findById(id).orElse(null);
            if (entity != null) {
                budgetCategoryRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Integer deleteMultiple(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return null;
        int result = 0;
        for (UUID id : ids) {
            deleteById(id);
            result++;
        }
        return result;
    }

    @Override
    public Boolean checkCode(BudgetCategoryDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<BudgetCategory> entities = budgetCategoryRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<BudgetCategory> entities = budgetCategoryRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (BudgetCategory entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }
}
