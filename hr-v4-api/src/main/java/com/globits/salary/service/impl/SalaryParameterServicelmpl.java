package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryParameter;
import com.globits.salary.dto.SalaryParameterDto;
import com.globits.salary.dto.SalaryUnitDto;
import com.globits.salary.repository.SalaryParameterRepository;
import com.globits.salary.service.SalaryParameterService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Transactional
@Service
public class SalaryParameterServicelmpl extends GenericServiceImpl<SalaryParameter, UUID> implements SalaryParameterService {

    @Autowired
    SalaryParameterRepository salaryParameterRepository;

    @Override
    public SalaryParameterDto saveSalaryParameter(SalaryParameterDto dto) {
        if(dto == null) return null;
        SalaryParameter entity = null;
        if(dto.getId() != null) {
            entity = salaryParameterRepository.findById(dto.getId()).orElse(null);
            if(entity == null) return null;
        }
        if(entity == null) entity = new SalaryParameter();

        entity.setLeaveApprovalLockDate(dto.getLeaveApprovalLockDate());
        entity.setLeaveApprovalLockTime(dto.getLeaveApprovalLockTime());
        entity.setMonthlyPayrollLockDate(dto.getMonthlyPayrollLockDate());
        entity.setMonthlyPayrollLockTime(dto.getMonthlyPayrollLockTime());
        entity.setDefaultPayrollCycleStart(dto.getDefaultPayrollCycleStart());
        entity.setMidCyclePayrollStart(dto.getMidCyclePayrollStart());
        entity.setApproveAttendanceDuration(dto.getApproveAttendanceDuration());
        entity.setCanAdminUpdate(dto.getCanAdminUpdate());
        entity.setEffectiveDate(dto.getEffectiveDate());
        entity = salaryParameterRepository.save(entity);

        if(entity == null) return null;
        return new SalaryParameterDto(entity);
    }
    @Override
    public SalaryParameterDto getById(UUID id) {
        SalaryParameter entity = salaryParameterRepository.findById(id).orElse(null);
        if(entity != null){
            return new SalaryParameterDto(entity);
        }
        return null;
    }

    @Override
    public  Boolean deleteSalaryParameter(UUID id) {
        try{
            Optional<SalaryParameter> entity = salaryParameterRepository.findById(id);
            if(entity.isPresent()){
                salaryParameterRepository.deleteById(id);
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error deleting SalaryUnit: " + e.getMessage());
        }
        return false;
    }

    @Override
    public  Boolean deleteMultipleSalaryParameters(List<UUID> listIds) {
        if (listIds == null || listIds.size() < 1) return null;
        boolean isValid = true;
        for (UUID id : listIds) {
            boolean statusDelete = this.deleteSalaryParameter(id);
            if (!statusDelete) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<SalaryParameterDto> pagingSalaryParameters(SearchDto dto) {
        if (dto == null) {
            return Page.empty();
        }

        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        // Construct the where clause and order by clause
        String whereClause = " WHERE (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "SELECT COUNT(DISTINCT entity.id) FROM SalaryParameter AS entity ";
        String sql = "SELECT DISTINCT new com.globits.salary.dto.SalaryParameterDto(entity) FROM SalaryParameter AS entity ";

        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            whereClause += " AND (entity.leaveApprovalLockTime LIKE :text "
                    + "OR entity.monthlyPayrollLockTime LIKE :text "
                    + "OR entity.midCyclePayrollStart LIKE :text) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, SalaryParameterDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        List<SalaryParameterDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        entities = query.getResultList();

        return new PageImpl<>(entities, pageable, count);
    }


}
