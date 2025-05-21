package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.HrDocumentItemDto;
import com.globits.hr.dto.HrDocumentTemplateDto;
import com.globits.hr.dto.search.SearchHrDocumentItemDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.HrDocumentItemService;
import com.globits.hr.service.HrDocumentTemplateService;
import com.globits.salary.domain.SalaryItem;
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
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class HrDocumentItemServiceImpl extends GenericServiceImpl<HrDocumentItem, UUID> implements HrDocumentItemService {
    @Autowired
    private HrDocumentItemRepository hrDocumentItemRepository;
    @Autowired
    private HrDocumentTemplateRepository hrDocumentTemplateRepository;
    @Autowired
    private HrDocumentTemplateService hrDocumentTemplateService;

    @Override
    public HrDocumentItemDto getById(UUID id) {
        if (id == null) return null;
        HrDocumentItem entity = hrDocumentItemRepository.findById(id).orElse(null);

        if (entity == null) return null;
        HrDocumentItemDto response = new HrDocumentItemDto(entity);

        return response;
    }

    @Override
    @Modifying
    public HrDocumentItemDto saveHrDocumentItem(HrDocumentItemDto dto) {
        if (dto == null) {
            return null;
        }

        HrDocumentItem entity = new HrDocumentItem();
        if (dto.getId() != null) entity = hrDocumentItemRepository.findById(dto.getId()).orElse(null);
        if (entity == null) entity = new HrDocumentItem();

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setRequired(dto.getIsRequired());

        if (dto.getDocumentTemplate() != null && dto.getDocumentTemplate().getId() != null) {
            HrDocumentTemplate entityTemplate = hrDocumentTemplateRepository.findById(dto.getDocumentTemplate().getId()).orElse(null);
            if (entityTemplate != null) {
                entity.setDocumentTemplate(entityTemplate);
            }
        }

        entity = hrDocumentItemRepository.save(entity);

        return new HrDocumentItemDto(entity);
    }

    @Override
    @Transactional
    public Integer saveListHrDocumentItems(List<HrDocumentItemDto> dtos) {
        int result = 0;
        if (dtos == null || dtos.isEmpty()) return result;
        for (HrDocumentItemDto dto : dtos) {
            HrDocumentItem entity = new HrDocumentItem();
            entity.setName(dto.getName());
            entity.setCode(dto.getCode());
            entity.setDescription(dto.getDescription());
            entity.setDisplayOrder(entity.getDisplayOrder());

            hrDocumentItemRepository.save(entity);
            result++;
        }
        return result;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteHrDocumentItem(UUID id) {
        if (id == null) return false;

        HrDocumentItem entity = hrDocumentItemRepository.findById(id).orElse(null);
        if (entity == null) return false;

        hrDocumentItemRepository.delete(entity);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultipleHrDocumentItems(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteHrDocumentItem(itemId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<HrDocumentItemDto> pagingHrDocumentItem(SearchHrDocumentItemDto dto) {
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
        String orderBy = " ORDER BY entity.displayOrder, entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from HrDocumentItem as entity ";
        String sql = "select distinct new com.globits.hr.dto.HrDocumentItemDto(entity) from HrDocumentItem as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text " +
                    "OR entity.description LIKE :text ) ";
        }
        if (dto.getDocumentTemplateId() != null) {
            whereClause += " AND entity.documentTemplate.id = :documentTemplateId ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, HrDocumentItemDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getDocumentTemplateId() != null) {
            query.setParameter("documentTemplateId", dto.getDocumentTemplateId());
            qCount.setParameter("documentTemplateId", dto.getDocumentTemplateId());
        }
        long count = (long) qCount.getSingleResult();


        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<HrDocumentItemDto> entities = query.getResultList();
        Page<HrDocumentItemDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public HrDocumentItemDto findByCode(String code) {
        List<HrDocumentItem> entities = hrDocumentItemRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new HrDocumentItemDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean isValidCode(HrDocumentItemDto dto) {
        if (dto == null)
            return false;
        if (dto.getDocumentTemplate() != null) {
            if (dto.getDocumentTemplate().getDocumentItems() == null) {
                dto.getDocumentTemplate().setDocumentItems(new ArrayList<>());
            }
            HrDocumentItemDto hrDocumentItemDto = new HrDocumentItemDto();
            hrDocumentItemDto.setCode(dto.getCode());
            hrDocumentItemDto.setName(dto.getName());
            hrDocumentItemDto.setDescription(dto.getDescription());
            hrDocumentItemDto.setDisplayOrder(dto.getDisplayOrder());
            hrDocumentItemDto.setIsRequired(dto.getIsRequired());

            dto.getDocumentTemplate().getDocumentItems().add(hrDocumentItemDto);

            return hrDocumentTemplateService.isValidCode(dto.getDocumentTemplate());
        }
        return true;
    }
}