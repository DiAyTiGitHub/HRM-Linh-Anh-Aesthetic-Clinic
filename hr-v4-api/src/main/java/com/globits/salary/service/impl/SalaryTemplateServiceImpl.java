package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchSalaryTemplateDto;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.SalaryTemplateItemDto;
import com.globits.salary.dto.SalaryTemplateItemGroupDto;
import com.globits.salary.repository.SalaryItemRepository;
import com.globits.salary.repository.SalaryTemplateItemGroupRepository;
import com.globits.salary.repository.SalaryTemplateItemRepository;
import com.globits.salary.repository.SalaryTemplateRepository;
import com.globits.salary.service.SalaryTemplateItemGroupService;
import com.globits.salary.service.SalaryTemplateItemService;
import com.globits.salary.service.SalaryTemplateService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
@Service
public class SalaryTemplateServiceImpl extends GenericServiceImpl<SalaryTemplate, UUID>
        implements SalaryTemplateService {

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryTemplateItemRepository templateItemRepository;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryTemplateItemGroupRepository templateItemGroupRepository;

    @Autowired
    private SalaryTemplateItemService salaryTemplateItemService;

    @Autowired
    private SalaryTemplateItemGroupService salaryTemplateItemGroupService;

    @Override
    public Boolean isValidCode(SalaryTemplateDto dto) {
        if (dto == null)
            return false;

        // ID of SalaryTemplate is null => Create new SalaryTemplate
        // => Assure that there's no other SalaryItems using this code of new SalaryItem
        // if there was any SalaryItem using new SalaryItem code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<SalaryTemplate> entities = salaryTemplateRepository.findByCode(dto.getCode());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            return false;

        }
        // ID of SalaryItem is NOT null => SalaryItem is modified
        // => Assure that the modified code is not same to OTHER any SalaryItem's code
        // if there was any SalaryItem using new SalaryItem code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<SalaryTemplate> entities = salaryTemplateRepository.findByCode(dto.getCode());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            for (SalaryTemplate entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public SalaryTemplateDto saveSalaryTemplate(SalaryTemplateDto dto) {
        if (dto == null)
            return null;

        SalaryTemplate entity = null;
        if (dto.getId() != null) {
            entity = this.findById(dto.getId());
        }
        if (entity == null) {
            entity = new SalaryTemplate();
            if (dto.getId() != null) entity.setId(dto.getId());
        }

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        if (dto.getIsCreatePayslip() == null || dto.getIsCreatePayslip().equals(false)) {
            entity.setIsCreatePayslip(false);
        } else {
            entity.setIsCreatePayslip(true);
        }

        if (dto.getIsActive() == null || dto.getIsActive().equals(false)) {
            entity.setIsActive(false);
        } else {
            entity.setIsActive(true);
        }

        entity = salaryTemplateRepository.save(entity);

        salaryTemplateItemGroupService.handleSetSalaryTemplateItemGroups(entity, dto);

        salaryTemplateItemService.handleSetSalaryTemplateItems(entity, dto);

        SalaryTemplate response = salaryTemplateRepository.save(entity);
        return new SalaryTemplateDto(response);
    }


    @Override
    public Boolean deleteSalaryTemplate(UUID id) {
        SalaryTemplate salaryItem = this.findById(id);
        if (salaryItem != null) {
            salaryItem.setVoided(true);
            salaryTemplateRepository.save(salaryItem);
            return true;
        }
        return false;
    }

    @Override
    public SalaryTemplateDto getSalaryTemplate(UUID id) {
        if (id == null)
            return null;

        SalaryTemplate salaryItem = this.findById(id);

        if (salaryItem == null)
            return null;

        return new SalaryTemplateDto(salaryItem, true);
    }

    @Override
    public Page<SalaryTemplateDto> searchByPage(SearchSalaryTemplateDto dto) {
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
        boolean voided = (dto.getVoided() == null || dto.getVoided() == false) ? false : true;
        String whereClause = "";
        if (voided) {
            whereClause = " AND (entity.voided = true) ";
        } else {
            whereClause = " AND (entity.voided = false OR entity.voided IS NULL) ";
        }
        String orderBy = " ORDER BY entity.modifyDate desc, entity.createDate desc ";

        String sqlCount = "select count(entity.id) from SalaryTemplate as entity where (1=1) ";
        String sql = "select new  com.globits.salary.dto.SalaryTemplateDto(entity) from SalaryTemplate as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }
        if (dto.getIsCreatePayslip() != null && dto.getIsCreatePayslip().equals(true)) {
            whereClause += " AND (entity.isCreatePayslip = true) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, SalaryItemDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<SalaryTemplateDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteSalaryTemplate(itemId);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }

    @Override
    public SalaryTemplateDto findByCode(String code) {
        List<SalaryTemplate> entities = salaryTemplateRepository.findByCode(code);
        if (entities == null || entities.isEmpty()) {
            return null;
        }
        return new SalaryTemplateDto(entities.get(0), true);
    }

    @Override
    public SalaryTemplateDto clonSalaryTemplate(SalaryTemplateDto dto) {
        if (dto.getId() == null) {
            return null;
        }
        SalaryTemplateDto result = this.getSalaryTemplate(dto.getId());
        if (result == null) {
            return null;
        }
        result.setId(null);
        result.setName(dto.getName());
        result.setCode(dto.getCode());
        result.setDescription(dto.getDescription());

        List<SalaryTemplateItemGroupDto> templateItemGroupsClon = result.getTemplateItemGroups();
        result.setTemplateItemGroups(null);

        List<SalaryTemplateItemDto> templateItemsClon = result.getTemplateItems();
        result.setTemplateItems(null);

        result = this.saveSalaryTemplate(result);

        // clon
        Map<UUID, UUID> mapSalaryTemplateItemGroupId = new HashMap<>();

        if (templateItemGroupsClon != null && templateItemGroupsClon.size() > 0) {
            for (SalaryTemplateItemGroupDto salaryTemplateItemGroupDto : templateItemGroupsClon) {
                UUID salaryTemplateItemGroupId = UUID.randomUUID();
                mapSalaryTemplateItemGroupId.put(salaryTemplateItemGroupDto.getId(), salaryTemplateItemGroupId);

                salaryTemplateItemGroupDto.setId(salaryTemplateItemGroupId);
                salaryTemplateItemGroupDto.setSalaryTemplateId(result.getId());
            }
            result.setTemplateItemGroups(templateItemGroupsClon);

        }

        if (templateItemsClon != null && templateItemsClon.size() > 0) {
            for (SalaryTemplateItemDto salaryTemplateItemDto : templateItemsClon) {
                UUID salaryTemplateItemGroupId = UUID.randomUUID();
                salaryTemplateItemDto.setId(salaryTemplateItemGroupId);

                if (salaryTemplateItemDto.getTemplateItemGroupId() != null) {
                    UUID templateItemGroupIdOld = salaryTemplateItemDto.getTemplateItemGroupId();
                    UUID templateItemGroupIdNew = mapSalaryTemplateItemGroupId.get(templateItemGroupIdOld);
                    if (templateItemGroupIdNew != null) {
                        salaryTemplateItemDto.setTemplateItemGroupId(templateItemGroupIdNew);
                    } else {
                        salaryTemplateItemDto.setTemplateItemGroupId(null);
                    }
                }
                salaryTemplateItemDto.setSalaryTemplateId(result.getId());
            }
            result.setTemplateItems(templateItemsClon);
        }
        result = this.saveSalaryTemplate(result);
        return result;
    }

}
