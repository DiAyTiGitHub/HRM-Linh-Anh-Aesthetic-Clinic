package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.repository.StaffRepository;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import com.globits.salary.repository.SalaryItemRepository;
import com.globits.salary.repository.SalaryItemThresholdRepository;
import com.globits.salary.repository.SalaryTemplateItemConfigRepository;
import com.globits.salary.service.SalaryItemService;
import com.globits.salary.service.SalaryItemThresholdService;
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
import java.util.stream.Collectors;

@Transactional
@Service
public class SalaryItemServiceImpl extends GenericServiceImpl<SalaryItem, UUID> implements SalaryItemService {

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryItemThresholdRepository salaryItemThresholdRepository;

    @Autowired
    private SalaryItemThresholdService salaryItemThresholdService;
    @Autowired
    private SalaryTemplateItemConfigRepository salaryTemplateItemConfigRepository;

    @Autowired
    private StaffRepository staffRepository;

    public boolean isSystemDefault(String code) {
        for (HrConstants.SalaryItemCodeSystemDefault item : HrConstants.SalaryItemCodeSystemDefault.values()) {
            if (item.name().equalsIgnoreCase(code) || item.getValue().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isValidCode(SalaryItemDto dto) {
        if (dto == null)
            return false;

        // ID of SalaryItem is null => Create new SalaryItem
        // => Assure that there's no other SalaryItems using this code of new SalaryItem
        // if there was any SalaryItem using new SalaryItem code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<SalaryItem> entities = salaryItemRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
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
            List<SalaryItem> entities = salaryItemRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (SalaryItem entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public SalaryItemDto findByCode(String code) {
        List<SalaryItem> entities = salaryItemRepository.findByCode(code);
        if (entities == null || entities.isEmpty()) {
            return null;
        }
        return new SalaryItemDto(entities.get(0), true);
    }

    @Override
    public List<SalaryItemDto> getByStaffId(UUID staffId) {
        if (staffId == null) return null;

        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) return null;

        List<SalaryItem> listEntity = salaryItemRepository.findByStaffId(staffId);

        List<SalaryItemDto> listDto = listEntity.stream()
                .filter(entity -> !entity.getId().equals(staffId))
                .map(entity -> new SalaryItemDto(entity)) // <-- chuyển sang DTO
                .collect(Collectors.toList());

        return listDto;
    }

    @Override
    public Integer saveListSalaryItem(List<SalaryItemDto> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<SalaryItem> listEntity = new ArrayList<>();
        for (SalaryItemDto dto : list) {
            SalaryItem entity = null;
            if (dto.getCode() == null) {
                continue;
            }

            List<SalaryItem> salaryItemList = salaryItemRepository.findByCode(dto.getCode());
            if (salaryItemList != null && !salaryItemList.isEmpty()) {
                entity = salaryItemList.get(0);
            }
            if (entity == null) {
                entity = new SalaryItem();
            }
            entity.setName(dto.getName());
            entity.setCode(dto.getCode());
            entity.setType(dto.getType());
            entity.setCalculationType(dto.getCalculationType());
            entity.setValueType(dto.getValueType());
            entity.setDefaultValue(dto.getDefaultValue());
            entity.setDescription(dto.getDescription());

            listEntity.add(entity);
        }

        listEntity = salaryItemRepository.saveAll(listEntity);
        return listEntity.size();
    }


    @Override
    public SalaryItemDto saveSalaryItem(SalaryItemDto dto) {
        if (dto == null || dto.getCode() == null || !StringUtils.hasText(dto.getCode()))
            return null;

        SalaryItem entity = null;
        if (dto.getId() != null) {
            entity = this.findById(dto.getId());
            if (entity == null)
                return null;
        }
        if (entity == null)
            entity = new SalaryItem();

        boolean isSystemDefault = this.isSystemDefault(entity.getCode());
        if (!isSystemDefault) {
            entity.setCode(dto.getCode().trim());
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getType());

        if (dto.getIsTaxable() == null || dto.getIsTaxable().equals(false)) {
            entity.setIsTaxable(false);
        } else {
            entity.setIsTaxable(true);
        }

        if (dto.getIsInsurable() == null || dto.getIsInsurable().equals(false)) {
            entity.setIsInsurable(false);
        } else {
            entity.setIsInsurable(true);
        }

        if (dto.getIsActive() == null || dto.getIsActive().equals(false)) {
            entity.setIsActive(false);
        } else {
            entity.setIsActive(true);
        }

        entity.setMaxValue(dto.getMaxValue());
        entity.setCalculationType(dto.getCalculationType());
        entity.setFormula(dto.getFormula());
        entity.setValueType(dto.getValueType());
        entity.setDefaultValue(dto.getDefaultValue());

        // save threshold if salary item's calculationType is THRESHOLD
        salaryItemThresholdService.handleSetInSalaryItem(entity, dto);


        SalaryItem response = salaryItemRepository.save(entity);
        return new SalaryItemDto(response);
    }

    @Override
    public Boolean deleteSalaryItem(UUID id) {
        SalaryItem salaryItem = this.findById(id);
        if (salaryItem == null)
            return false;

        // cannot update Item that is system default
//        boolean isSystemDefault = isSystemDefault(salaryItem.getCode());
//        if (isSystemDefault)
//            return false;

//        if (salaryItem.getCalculationType() != null && salaryItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue()))
//            return false;

        salaryItemRepository.deleteById(id);
        return true;
    }

    @Override
    public SalaryItemDto getSalaryItem(UUID id) {
        if (id == null)
            return null;

        SalaryItem salaryItem = this.findById(id);

        if (salaryItem == null)
            return null;

        return new SalaryItemDto(salaryItem, true);
    }

    @Override
    public Page<SalaryItemDto> searchByPage(SearchSalaryItemDto dto) {
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
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(entity.id) from SalaryItem as entity where (1=1) ";
        String sql = "select new  com.globits.salary.dto.SalaryItemDto(entity) from SalaryItem as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }
        if (dto.getType() != null) {
            whereClause += " and (entity.type = :type) ";
        }
        if (dto.getIsTaxable() != null) {
            whereClause += " and (entity.isTaxable = :isTaxable) ";
        }
        if (dto.getIsInsurable() != null) {
            whereClause += " and (entity.isInsurable = :isInsurable) ";
        }
        if (dto.getIsActive() != null) {
            whereClause += " and (entity.isActive = :isActive) ";
        }
        if (dto.getCalculationType() != null) {
            whereClause += " and (entity.calculationType = :calculationType) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, SalaryItemDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getType() != null) {
            q.setParameter("type", dto.getType());
            qCount.setParameter("type", dto.getType());
        }
        if (dto.getIsTaxable() != null) {
            q.setParameter("isTaxable", dto.getIsTaxable());
            qCount.setParameter("isTaxable", dto.getIsTaxable());
        }
        if (dto.getIsInsurable() != null) {
            q.setParameter("isInsurable", dto.getIsInsurable());
            qCount.setParameter("isInsurable", dto.getIsInsurable());
        }
        if (dto.getIsActive() != null) {
            q.setParameter("isActive", dto.getIsActive());
            qCount.setParameter("isActive", dto.getIsActive());
        }
        if (dto.getCalculationType() != null) {
            q.setParameter("calculationType", dto.getCalculationType());
            qCount.setParameter("calculationType", dto.getCalculationType());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<SalaryItemDto> entities = q.getResultList();
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
            boolean deleteRes = this.deleteSalaryItem(itemId);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }


}
