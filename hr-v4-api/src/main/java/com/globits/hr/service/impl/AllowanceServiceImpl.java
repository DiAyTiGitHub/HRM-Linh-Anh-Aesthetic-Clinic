package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Allowance;
import com.globits.hr.domain.AllowanceType;
import com.globits.hr.dto.AllowanceDto;
import com.globits.hr.dto.SearchAllowanceDto;
import com.globits.hr.repository.AllowanceRepository;
import com.globits.hr.repository.AllowanceTypeRepository;
import com.globits.hr.service.AllowanceService;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.repository.SalaryItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.persistence.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class AllowanceServiceImpl extends GenericServiceImpl<Allowance, UUID> implements AllowanceService {
    @Autowired
    private AllowanceRepository allowanceRepository;

    @Autowired
    private AllowanceTypeRepository allowanceTypeRepository;
    
    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Override
    public AllowanceDto saveOrUpdate(AllowanceDto dto, UUID id) {
        if (dto == null) return null;

        Allowance entity = null;
        if (id != null) {
            entity = allowanceRepository.findById(id).orElse(null);
        }
        if (entity == null && dto.getId() != null) {
            entity = allowanceRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new Allowance();
        }

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());

        if (dto.getAllowanceType() != null && dto.getAllowanceType().getId() != null) {
            AllowanceType allowanceType = allowanceTypeRepository.findById(dto.getAllowanceType().getId()).orElse(null);
            if (allowanceType != null) {
                entity.setAllowanceType(allowanceType);
            }
        }

        entity = allowanceRepository.save(entity);

        this.createSalaryItem(dto, entity);
        
        return new AllowanceDto(entity);
    }


    @Override
    public void deleteAllowance(UUID id) {
        Allowance entity = null;
        Optional<Allowance> optional = allowanceRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            allowanceRepository.delete(entity);
        }
    }
    
	@Override
	@Transactional
	public Boolean deleteMultiple(List<UUID> ids) {
		if (ids == null)
			return false;
		for (UUID applicantId : ids) {
			this.deleteAllowance(applicantId);
		}
		return true;
	}
	
	private void createSalaryItem(AllowanceDto dto, Allowance entity) {
		try {
        	SalaryItem salaryItem = null;
        	if (entity.getSalaryItem() != null) {
        		salaryItem = salaryItemRepository.findById(entity.getSalaryItem().getId()).orElse(null);
        	} else {
        		List<SalaryItem> salaryItemList = salaryItemRepository.findByCode(entity.getCode());
        		if (salaryItemList != null && salaryItemList.size() > 0) {
        			return;
        		}
        	}
        	
        	if (salaryItem == null) {
                salaryItem = new SalaryItem();
            } 
        	// lay theo ma ten cua phu cap
            salaryItem.setCode(entity.getCode());
            salaryItem.setName(entity.getName());
            
            salaryItem.setDescription(dto.getSalaryItem().getDescription());
            salaryItem.setType(dto.getSalaryItem().getType());
            salaryItem.setIsTaxable(dto.getSalaryItem().getIsTaxable());
            salaryItem.setIsInsurable(dto.getSalaryItem().getIsInsurable());
            salaryItem.setIsActive(dto.getSalaryItem().getIsActive());
            salaryItem.setMaxValue(dto.getSalaryItem().getMaxValue());
            salaryItem.setDefaultValue(dto.getSalaryItem().getDefaultValue());
            salaryItem.setCalculationType(dto.getSalaryItem().getCalculationType());
            salaryItem.setFormula(dto.getSalaryItem().getFormula());
            salaryItem.setValueType(dto.getSalaryItem().getValueType());
            
            salaryItem.setAllowance(entity);

            salaryItem = salaryItemRepository.save(salaryItem);

            entity.setSalaryItem(salaryItem);
            entity = allowanceRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException("Lỗi khi tạo hoặc cập nhật SalaryItem", e);
        }
	}

    @Override
    public AllowanceDto getAllowance(UUID id) {
        Allowance entity = null;
        Optional<Allowance> optional = allowanceRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new AllowanceDto(entity);
        }
        return null;
    }

    @Override
    public Page<AllowanceDto> searchByPage(SearchAllowanceDto dto) {
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
        String orderBy = " ORDER BY entity.id DESC";
        String sqlCount = "select count(entity.id) from Allowance as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.AllowanceDto(entity) from Allowance as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) ) OR ( UPPER(entity.code) LIKE UPPER(:text) )";
        }
        
        if (dto.getAllowanceTypeId() != null) {
        	whereClause += " AND entity.allowanceType.id = :allowanceTypeId ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, AllowanceDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getAllowanceTypeId() != null) {
            q.setParameter("allowanceTypeId", dto.getAllowanceTypeId());
            qCount.setParameter("allowanceTypeId", dto.getAllowanceTypeId());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<AllowanceDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }
    
    @Override
    public Boolean isValidCode(AllowanceDto dto) {
        if (dto == null)
            return false;
        List<Allowance> entities = allowanceRepository.findByCode(dto.getCode());
        if (entities == null || entities.isEmpty()) {
            return true;
        }
        if (dto.getId() == null) {
            return false;
        } else {
            for (Allowance entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }
}
