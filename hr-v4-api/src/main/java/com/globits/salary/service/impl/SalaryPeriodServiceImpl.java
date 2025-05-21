package com.globits.salary.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.service.SystemConfigService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.repository.SalaryPeriodRepository;
import com.globits.salary.service.SalaryPeriodService;
import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SalaryPeriodServiceImpl extends GenericServiceImpl<SalaryPeriod, UUID> implements SalaryPeriodService {
    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    @Modifying
    @Transactional
    public SalaryPeriodDto saveOrUpdate(SalaryPeriodDto dto) {
        if (dto == null) {
            return null;
        }

        SalaryPeriod entity = new SalaryPeriod();
        if (dto.getId() != null)
            entity = salaryPeriodRepository.findById(dto.getId()).orElse(null);
        if (entity == null)
            entity = new SalaryPeriod();

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());

        // Ngày đầu tiên trong kỳ lương
        entity.setFromDate(dto.getFromDate());
        if (entity.getFromDate() != null) {
            entity.setFromDate(DateTimeUtil.getStartOfDay(entity.getFromDate()));
        }

        // Ngày cuối cùng trong kỳ lương
        entity.setToDate(dto.getToDate());
        if (entity.getToDate() != null) {
            entity.setToDate(DateTimeUtil.getEndOfDay(entity.getToDate()));
        }

        if (dto.getParentPeriod() != null) {
            if (dto.getParentPeriod().getId() != null && dto.getParentPeriod().getId().equals(entity.getId()))
                return null;
            SalaryPeriod parent = salaryPeriodRepository.findById(dto.getParentPeriod().getId()).orElse(null);
            entity.setParentPeriod(parent);
        } else {
            entity.setParentPeriod(null);
        }

        if (entity.getSubPeriods() == null) {
            entity.setSubPeriods(new HashSet<>());
        }

        Set<SalaryPeriod> subPeriods = new HashSet<>();
        if (dto.getSubPeriods() != null && dto.getSubPeriods().size() > 0) {
            for (SalaryPeriodDto periodDto : dto.getSubPeriods()) {
                if (periodDto == null || periodDto.getId() == null)
                    continue;

                SalaryPeriod period = salaryPeriodRepository.findById(periodDto.getId()).orElse(null);
                if (period == null)
                    continue;

                period.setParentPeriod(entity);

                subPeriods.add(period);
            }
        }
        entity.getSubPeriods().clear();
        entity.getSubPeriods().addAll(subPeriods);

        entity = salaryPeriodRepository.save(entity);

        return new SalaryPeriodDto(entity);
    }

    @Override
    public Page<SalaryPeriodDto> searchByPage(SearchDto searchDto) {
        if (searchDto == null) {
            return null;
        }

        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.fromDate desc, entity.toDate desc, entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from SalaryPeriod as entity ";
        String sql = "select distinct new com.globits.salary.dto.SalaryPeriodDto(entity) from SalaryPeriod as entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        if (searchDto.getFromDate() != null) {
            whereClause += " AND entity.fromDate >= :fromDate ";
        }

        if (searchDto.getToDate() != null) {
            whereClause += " AND entity.toDate <= :toDate ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, SalaryPeriodDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }

        if (searchDto.getFromDate() != null) {
            query.setParameter("fromDate", searchDto.getFromDate());
            qCount.setParameter("fromDate", searchDto.getFromDate());
        }

        if (searchDto.getToDate() != null) {
            query.setParameter("toDate", searchDto.getToDate());
            qCount.setParameter("toDate", searchDto.getToDate());
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;

        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<SalaryPeriodDto> entities = query.getResultList();
        Page<SalaryPeriodDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public SalaryPeriodDto getById(UUID id) {
        SalaryPeriod optionalPayrollPeriod = salaryPeriodRepository.findById(id).orElse(null);
        if (optionalPayrollPeriod == null)
            return null;

        return new SalaryPeriodDto(optionalPayrollPeriod, true);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean remove(UUID id) {
        if (id == null)
            return false;

        SalaryPeriod payrollPeriod = salaryPeriodRepository.findById(id).orElse(null);
        if (payrollPeriod == null)
            return false;

        salaryPeriodRepository.delete(payrollPeriod);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean removeMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.remove(id);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }

    @Override
    public SalaryPeriodDto findByCode(String code) {
        if (code == null || code.isEmpty())
            return null;
        List<SalaryPeriod> entities = salaryPeriodRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new SalaryPeriodDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean isValidCode(SalaryPeriodDto dto) {
        if (dto == null)
            return false;

        // ID of SalaryPeriod is null => Create new SalaryPeriod
        // => Assure that there's no other SalaryPeriods using this code of new
        // SalaryPeriod
        // if there was any SalaryPeriod using new SalaryPeriod code, then this new code
        // is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<SalaryPeriod> entities = salaryPeriodRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            return false;

        }
        // ID of SalaryPeriod is NOT null => SalaryPeriod is modified
        // => Assure that the modified code is not same to OTHER any SalaryPeriod code
        // if there was any SalaryPeriod using new SalaryPeriod code, then this new code
        // is
        // invalid => return False
        // else return true
        else {
            List<SalaryPeriod> entities = salaryPeriodRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            for (SalaryPeriod entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }


    @Override
    public List<SalaryPeriodDto> findSalaryPeriodsInRangeTime(Date fromDate, Date toDate) {
        List<SalaryPeriodDto> response = new ArrayList<>();

        if (fromDate == null || toDate == null) return response;

        List<SalaryPeriod> overlapPeriods = salaryPeriodRepository.findSalaryPeriodsOverlapWithRange(fromDate, toDate);

        for (SalaryPeriod period : overlapPeriods) {
            response.add(new SalaryPeriodDto(period));
        }

        return response;
    }


    @Override
    public List<SalaryPeriodDto> getActivePeriodsByDate(Date requestDate) {
        List<SalaryPeriodDto> response = new ArrayList<>();

        if (requestDate == null) return response;

        List<SalaryPeriod> activePeriods = salaryPeriodRepository.getActivePeriodsByDate(requestDate);

        if (activePeriods == null || activePeriods.isEmpty()) return response;

        for (SalaryPeriod salaryPeriod : activePeriods) {
            response.add(new SalaryPeriodDto(salaryPeriod));
        }

        return response;
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = salaryPeriodRepository.findMaxCodeByPrefix(systemConfig.getConfigValue());
            return systemConfigService.generateNextCodeKL(systemConfig.getConfigValue(), maxCode);
        }
    }

}
