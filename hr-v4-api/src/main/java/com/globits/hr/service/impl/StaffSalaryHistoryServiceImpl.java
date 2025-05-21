package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffSalaryHistory;
import com.globits.hr.dto.StaffSalaryHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffSalaryHistoryRepository;
import com.globits.hr.service.StaffSalaryHistoryService;
import com.globits.salary.domain.SalaryIncrementType;
import com.globits.salary.repository.SalaryIncrementTypeRepository;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffSalaryHistoryServiceImpl extends GenericServiceImpl<StaffSalaryHistory, UUID> implements StaffSalaryHistoryService {
    @Autowired
    private StaffSalaryHistoryRepository staffSalaryHistoryRepository;
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    SalaryIncrementTypeRepository salaryIncrementTypeRepository;

    @Override
    public StaffSalaryHistoryDto getById(UUID id) {
        if (id != null) {
            StaffSalaryHistory entity = staffSalaryHistoryRepository.findById(id).orElse(null);
            if (entity != null) {
                return new StaffSalaryHistoryDto(entity);
            }
        }
        return null;
    }

    @Override
    public StaffSalaryHistoryDto saveOrUpdate(StaffSalaryHistoryDto dto) {
        if (dto != null) {
            StaffSalaryHistory entity = null;
            if (dto.getId() != null) {
                entity = staffSalaryHistoryRepository.findById(dto.getId()).orElse(null);
            }
            if (entity == null) {
                entity = new StaffSalaryHistory();
            }

            Staff staff = null;
            if (dto.getStaff() != null && dto.getStaff().getId() != null) {
                staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            }
            if (staff == null) return null;
            entity.setStaff(staff);
            entity.setDecisionCode(dto.getDecisionCode());
            entity.setDecisionDate(dto.getDecisionDate());
            entity.setCoefficient(dto.getCoefficient());
            entity.setCoefficientOverLevel(dto.getCoefficientOverLevel());
            entity.setPercentage(dto.getPercentage());
            entity.setStaffTypeCode(dto.getStaffTypeCode());
            if (dto.getSalaryIncrementType() != null) {
                SalaryIncrementType salaryType = null;
                Optional<SalaryIncrementType> optional = salaryIncrementTypeRepository.findById(dto.getSalaryIncrementType().getId());
                if (optional.isPresent()) {
                    salaryType = optional.get();
                }
                entity.setSalaryIncrementType(salaryType);

            }
            StaffSalaryHistory response = staffSalaryHistoryRepository.save(entity);
            return new StaffSalaryHistoryDto(response);
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            StaffSalaryHistory entity = staffSalaryHistoryRepository.findById(id).orElse(null);
            if (entity != null) {
                staffSalaryHistoryRepository.delete(entity);
                return true;
            }
        }
        return false;
    }

    @Override
    public Page<StaffSalaryHistoryDto> paging(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from StaffSalaryHistory as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.StaffSalaryHistoryDto(entity) from StaffSalaryHistory as entity where (1=1) ";

        if (dto.getStaffId() != null) {
            whereClause += " AND ( entity.staff.id = :staffId) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, StaffSalaryHistoryDto.class);
        Query qCount = manager.createQuery(sqlCount);
        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<StaffSalaryHistoryDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public List<StaffSalaryHistoryDto> getAllByStaff(UUID id) {
        if (id == null) return null;
        return staffSalaryHistoryRepository.getAllByStaffId(id);
    }
}
