package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.KPIResultDto;
import com.globits.hr.dto.KPIResultItemDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.KPIResultService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class KPIResultServiceImpl extends GenericServiceImpl<KPIResult, UUID> implements KPIResultService {
    @Autowired
    private KPIResultRepository kpiResultRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private KPIRepository kpiRepository;
    @Autowired
    private KPIResultItemRepository kpiResultItemRepository;
    @Autowired
    private KPIItemRepository kpiItemRepository;

    @Override
    public List<KPIResultDto> getAll() {
        List<KPIResult> kpiResults = kpiResultRepository.findAll();
        List<KPIResultDto> kpiResultDtos = new ArrayList<>();
        for (KPIResult kpiResult : kpiResults) {
            kpiResultDtos.add(new KPIResultDto(kpiResult));
        }
        return kpiResultDtos;
    }

    @Override
    public KPIResultDto getKPIById(UUID id) {
        if (id != null) {
            KPIResult kpiResult = kpiResultRepository.findById(id).orElse(null);
            if (kpiResult != null) {
                return new KPIResultDto(kpiResult);
            }
        }
        return null;
    }

    @Override
    public KPIResultDto saveOrUpdate(KPIResultDto dto) {
        if (dto != null) {
            KPIResult entity = null;
            if (dto.getId() != null) {
                entity = kpiResultRepository.findById(dto.getId()).orElse(null);
            }
            if (entity == null) {
                entity = new KPIResult();
            }
            if (dto.getStaff() != null && dto.getStaff().getId() != null) {
                Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
                entity.setStaff(staff);
            }
            if (dto.getKpi() != null && dto.getKpi().getId() != null) {
                KPI kpi = kpiRepository.findById(dto.getKpi().getId()).orElse(null);
                entity.setKpi(kpi);
            }
            if (entity.getKpiResultItems() == null) {
                entity.setKpiResultItems(new HashSet<>());
            }
            entity.getKpiResultItems().clear();
            for (KPIResultItemDto item : dto.getKpiResultItems()) {
                KPIResultItem kpiResultItem = null;
                if (item.getId() != null) {
                    kpiResultItem = kpiResultItemRepository.findById(item.getId()).orElse(null);
                }
                if (kpiResultItem == null) {
                    kpiResultItem = new KPIResultItem();
                }
                kpiResultItem.setKpiResult(entity);
                kpiResultItem.setValue(item.getValue());
                if (item.getKpiItem() != null && item.getKpiItem().getId() != null) {
                    KPIItem kpiItem = kpiItemRepository.findById(item.getKpiItem().getId()).orElse(null);
                    kpiResultItem.setKpiItem(kpiItem);
                }
                entity.getKpiResultItems().add(kpiResultItem);
            }
            KPIResult response = kpiResultRepository.save(entity);
            return new KPIResultDto(response);
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            KPIResult kpiResult = kpiResultRepository.findById(id).orElse(null);
            if (kpiResult != null) {
                kpiResultRepository.delete(kpiResult);
                return true;
            }
        }
        return false;
    }

    @Override
    public Page<KPIResultDto> paging(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from KPIResult as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.KPIResultDto(entity) from KPIResult as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.staff.displayName LIKE :text OR entity.kpi.code LIKE :text OR entity.kpi.name LIKE :text ) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, KPIResultDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<KPIResultDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }
}
