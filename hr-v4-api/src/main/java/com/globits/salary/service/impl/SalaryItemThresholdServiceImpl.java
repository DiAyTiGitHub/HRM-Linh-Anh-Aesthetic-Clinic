package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryItemThreshold;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryItemThresholdDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import com.globits.salary.repository.SalaryItemRepository;
import com.globits.salary.repository.SalaryItemThresholdRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class SalaryItemThresholdServiceImpl extends GenericServiceImpl<SalaryItemThreshold, UUID> implements SalaryItemThresholdService {
    @Autowired
    private SalaryItemThresholdRepository salaryItemThresholdRepository;

    // save threshold if salary item's calculationType is THRESHOLD
    @Override
    public void handleSetInSalaryItem(SalaryItem entity, SalaryItemDto dto) {
//        if (entity.getThresholds() == null) entity.setThresholds(new HashSet<>());
//        entity.getThresholds().clear();
//
//        if (entity.getCalculationType() != null && entity.getCalculationType().equals(HrConstants.SalaryItemCalculationType.THRESHOLD.getValue())
//                && dto.getThresholds() != null && !dto.getThresholds().isEmpty()) {
//            for (SalaryItemThresholdDto thresholdDto : dto.getThresholds()) {
//                SalaryItemThreshold item = null;
//
//                if (thresholdDto != null && thresholdDto.getId() != null) {
//                    item = salaryItemThresholdRepository.findById(thresholdDto.getId()).orElse(null);
//                }
//
//                if (item == null) {
//                    item = new SalaryItemThreshold();
//                    item.setSalaryItem(entity);
//                }
//
//                item.setDisplayOrder(thresholdDto.getDisplayOrder());
//                item.setThresholdValue(thresholdDto.getThresholdValue());
//                item.setInUseValue(thresholdDto.getInUseValue());
//
//                entity.getThresholds().add(item);
//            }
//        }

    }
}
