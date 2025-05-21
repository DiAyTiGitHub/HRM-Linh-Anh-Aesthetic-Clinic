package com.globits.hr.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.globits.core.domain.Department;
import com.globits.core.domain.Organization;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Allowance;
import com.globits.hr.domain.AllowancePolicy;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffAllowance;
import com.globits.hr.dto.AllowancePolicyDto;
import com.globits.hr.dto.StaffAllowanceDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.AllowancePolicyRepository;
import com.globits.hr.repository.AllowanceRepository;
import com.globits.hr.repository.StaffAllowanceRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffAllowanceService;

import jakarta.persistence.Query;

@Service
public class StaffAllowanceServiceImpl extends GenericServiceImpl<StaffAllowance, UUID> implements StaffAllowanceService {

    @Autowired
    private StaffAllowanceRepository staffAllowanceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private AllowanceRepository allowanceRepository;

    @Autowired
    private AllowancePolicyRepository allowancePolicyRepository;

    @Override
    public StaffAllowanceDto saveOrUpdate(StaffAllowanceDto dto) {
        if (dto == null) return null;

        StaffAllowance entity = null;

        if (dto.getId() != null) {
            entity = staffAllowanceRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        }
        if (entity == null) entity = new StaffAllowance();

        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setUsingFormula(dto.getUsingFormula());

        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            entity.setStaff(staff);
        }

        if (dto.getAllowance() != null && dto.getAllowance().getId() != null) {
            Allowance allowance = allowanceRepository.findById(dto.getAllowance().getId()).orElse(null);
            if (allowance != null) {
                entity.setAllowance(allowance);
            }
        } else {
            entity.setAllowance(null);
        }

        if (dto.getAllowancePolicy() != null && dto.getAllowancePolicy().getId() != null) {
            AllowancePolicy allowancePolicy = allowancePolicyRepository.findById(dto.getAllowancePolicy().getId()).orElse(null);
            if (allowancePolicy != null) {
                entity.setAllowancePolicy(allowancePolicy);
            }
        } else {
            entity.setAllowancePolicy(null);
        }

        entity = staffAllowanceRepository.save(entity);

        if (entity == null) return null;
        StaffAllowanceDto result = new StaffAllowanceDto(entity);
        return result;
    }

    @Override
    public void deleteStaffAllowance(UUID id) {
        if (id != null) {
            staffAllowanceRepository.deleteById(id);
        }
    }

    @Override
    public Page<StaffAllowanceDto> searchByPage(SearchDto dto) {
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
        String sqlCount = "select count(entity.id) from StaffAllowance as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.StaffAllowanceDto(entity) from StaffAllowance as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( UPPER(entity.staff.displayName) LIKE UPPER(:text) )";
        }

        if (dto.getStaffId() != null) {
            whereClause += " AND entity.staff.id =:staffId ";
        }
        if (dto.getAllowanceId() != null) {
            whereClause += " AND entity.allowance.id =:allowanceId ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, StaffAllowanceDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getAllowanceId() != null) {
            q.setParameter("allowanceId", dto.getAllowanceId());
            qCount.setParameter("allowanceId", dto.getAllowanceId());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);

        List<StaffAllowanceDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public StaffAllowanceDto getStaffAllowanceById(UUID id) {
        StaffAllowance entity = null;
        Optional<StaffAllowance> optional = staffAllowanceRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new StaffAllowanceDto(entity);
        }
        return null;
    }

	@Override
	public List<StaffAllowanceDto> getStaffAllowanceByStaffId(UUID staffId) {
		if (staffId == null) {
			return null;
		}
		List<StaffAllowanceDto> result = staffAllowanceRepository.findByStaffId(staffId);
		return result;
	}

}