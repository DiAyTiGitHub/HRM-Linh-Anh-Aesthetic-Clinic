package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.SalaryAreaService;
import com.globits.salary.service.SalaryItemService;
import com.globits.salary.service.SalaryItemThresholdService;
import com.globits.salary.service.SalaryTemplateItemConfigService;
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

import java.util.*;

@Service
public class SalaryTemplateItemConfigServiceImpl extends GenericServiceImpl<SalaryTemplateItemConfig, UUID> implements SalaryTemplateItemConfigService {
    @Autowired
    private SalaryTemplateItemConfigRepository salaryTemplateItemConfigRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;


    @Override
    public void handleSetToSalaryTemplateItems(SalaryTemplateItem entity, SalaryTemplateItemDto dto) {
        if (entity.getTemplateItemConfigs() == null) {
            entity.setTemplateItemConfigs(new HashSet<>());
        }

        Set<SalaryTemplateItemConfig> templateItemConfigs = new HashSet<>();

        if (dto.getTemplateItemConfigs() != null && !dto.getTemplateItemConfigs().isEmpty()) {
            int index = 0;

            for (SalaryTemplateItemConfigDto itemConfigDto : dto.getTemplateItemConfigs()) {
                SalaryTemplateItemConfig itemConfig;

                if (itemConfigDto.getId() != null) {
                    itemConfig = salaryTemplateItemConfigRepository.findById(itemConfigDto.getId()).orElse(new SalaryTemplateItemConfig());
                } else {
                    itemConfig = new SalaryTemplateItemConfig();
                }

                // Gán các giá trị từ DTO vào entity
                itemConfig.setCompareOrder(++index);
                itemConfig.setFormula(itemConfigDto.getFormula());
                itemConfig.setItemValue(itemConfigDto.getItemValue());
                itemConfig.setConfigType(itemConfigDto.getConfigType());

                itemConfig.setOperatorMinValue(itemConfigDto.getOperatorMinValue());
                itemConfig.setMinValue(itemConfigDto.getMinValue());
                if (itemConfig.getMinValue() == null && itemConfig.getOperatorMinValue() != null) {
                    itemConfig.setMinValue(0D);
                }

                itemConfig.setOperatorMaxValue(itemConfigDto.getOperatorMaxValue());
                itemConfig.setMaxValue(itemConfigDto.getMaxValue());
                if (itemConfig.getMaxValue() == null && itemConfig.getOperatorMaxValue() != null) {
                    itemConfig.setMaxValue(0D);
                }

                // Gán quan hệ
                itemConfig.setTemplateItem(entity);

                templateItemConfigs.add(itemConfig);
            }
        }

        entity.getTemplateItemConfigs().clear();
        entity.getTemplateItemConfigs().addAll(templateItemConfigs);
    }

}
