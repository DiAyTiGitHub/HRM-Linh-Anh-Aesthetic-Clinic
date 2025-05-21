package com.globits.hr.service.impl;

import com.globits.core.domain.Department;
import com.globits.core.domain.Organization;
import com.globits.core.repository.DepartmentRepository;
import com.globits.core.repository.OrganizationRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Allowance;
import com.globits.hr.domain.AllowancePolicy;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffAllowance;
import com.globits.hr.dto.AllowancePolicyDto;
import com.globits.hr.dto.StaffAllowanceDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.AllowancePolicyRepository;
import com.globits.hr.repository.AllowanceRepository;
import com.globits.hr.repository.PositionRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.AllowancePolicyService;
import com.globits.hr.service.StaffAllowanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.persistence.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AllowancePolicyServiceImpl extends GenericServiceImpl<AllowancePolicy, UUID> implements AllowancePolicyService {
	
    @Autowired
    private AllowancePolicyRepository allowancePolicyRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private AllowanceRepository allowanceRepository;
    
    @Autowired
    private StaffRepository staffRepository;

    @Override
    public AllowancePolicyDto saveOrUpdate(AllowancePolicyDto dto, UUID id) {
        if (dto == null) return null;

        AllowancePolicy entity = null;
        if (id != null) {
            entity = allowancePolicyRepository.findById(id).orElse(null);
        }
        if (entity == null && dto.getId() != null) {
            entity = allowancePolicyRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        }
        if (entity == null) entity = new AllowancePolicy();

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        
        entity.setFormula(dto.getFormula());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        
        if (dto.getOrganization() != null && dto.getOrganization().getId() != null) {
        	Organization organization = organizationRepository.findById(dto.getOrganization().getId()).orElse(null);
        	if (organization != null) {
        		entity.setOrganization(organization);
        	}
        } else {
        	entity.setOrganization(null);
        }
        
        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
        	Department department = departmentRepository.findById(dto.getDepartment().getId()).orElse(null);
        	if (department != null) {
        		entity.setDepartment(department);
        	}
        } else {
        	entity.setDepartment(null);
        }
        
        if (dto.getPosition() != null && dto.getPosition().getId() != null) {
        	Position position = positionRepository.findById(dto.getPosition().getId()).orElse(null);
        	if (position != null) {
        		entity.setPosition(position);
        	}
        } else {
        	entity.setPosition(null);
        }
        
        if (dto.getAllowance() != null && dto.getAllowance().getId() != null) {
        	Allowance allowance = allowanceRepository.findById(dto.getAllowance().getId()).orElse(null);
        	if (allowance != null) {
        		entity.setAllowance(allowance);
        	}
        } else {
        	entity.setAllowance(null);
        }
        
        this.generateStaffAllowances(entity, dto);
        
        entity = allowancePolicyRepository.save(entity);
        
        
        
        if (entity == null) return null;
        AllowancePolicyDto result = new AllowancePolicyDto(entity);
        return result;
    }

    public void generateStaffAllowances(AllowancePolicy entity, AllowancePolicyDto dto) {
        if (entity == null || dto == null) return;

        Set<StaffAllowance> currentAllowances = entity.getStaffAllowances();
        if (currentAllowances == null) {
            currentAllowances = new HashSet<>();
            entity.setStaffAllowances(currentAllowances);
        } else {
            currentAllowances.clear();
        }

        if (dto.getStaffs() != null && !dto.getStaffs().isEmpty()) {
            for (StaffDto staffDto : dto.getStaffs()) {
                if (staffDto.getId() == null) continue;
                
                Staff staff = staffRepository.findById(staffDto.getId()).orElse(null);
                if (staff == null) continue;

                StaffAllowance staffAllowance = new StaffAllowance();
                staffAllowance.setAllowancePolicy(entity);
                staffAllowance.setAllowance(entity.getAllowance());
                staffAllowance.setStaff(staff);
                staffAllowance.setUsingFormula(dto.getFormula());
                staffAllowance.setStartDate(dto.getStartDate());
                staffAllowance.setEndDate(dto.getEndDate());

                currentAllowances.add(staffAllowance);
            }
        }
    }

    
    
    @Override
    public void deleteAllowancePolicy(UUID id) {
    	AllowancePolicy entity = null;
        Optional<AllowancePolicy> optional = allowancePolicyRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            allowancePolicyRepository.delete(entity);
        }
    }
    

    @Override
    public AllowancePolicyDto getAllowancePolicyById(UUID id) {
    	AllowancePolicy entity = null;
        Optional<AllowancePolicy> optional = allowancePolicyRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new AllowancePolicyDto(entity, true);
        }
        return null;
    }

    @Override
    public Page<AllowancePolicyDto> searchByPage(SearchDto dto) {
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
        String sqlCount = "select count(entity.id) from AllowancePolicy as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.AllowancePolicyDto(entity) from AllowancePolicy as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) ) OR ( UPPER(entity.code) LIKE UPPER(:text) )";
        }
        
//        if (dto.getAllowanceId() != null) {
//        	whereClause += " AND entity.allowance.id = :allowanceId ";
//        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, AllowancePolicyDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
//        if (dto.getAllowanceId() != null) {
//            q.setParameter("allowanceId", dto.getAllowanceId());
//            qCount.setParameter("allowanceId", dto.getAllowanceId());
//        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<AllowancePolicyDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }
    
}
