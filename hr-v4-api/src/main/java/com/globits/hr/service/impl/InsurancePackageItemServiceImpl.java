package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.InsurancePackage;
import com.globits.hr.domain.InsurancePackageItem;
import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.InsurancePackageDto;
import com.globits.hr.dto.InsurancePackageItemDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.InsurancePackageItemRepository;
import com.globits.hr.repository.InsurancePackageRepository;
import com.globits.hr.service.InsurancePackageItemService;
import com.globits.hr.service.InsurancePackageService;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import com.globits.salary.dto.SalaryTemplateItemDto;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class InsurancePackageItemServiceImpl extends GenericServiceImpl<InsurancePackageItem, UUID> implements InsurancePackageItemService {
    @Autowired
    private InsurancePackageRepository insurancePackageRepository;

    @Autowired
    private InsurancePackageItemRepository insurancePackageItemRepository;


    @Override
    public void handleSetItemsForInsurancePackage(InsurancePackage entity, InsurancePackageDto dto) {
        if (entity == null || dto == null) return;
        if (entity.getPackageItems() == null)
            entity.setPackageItems(new HashSet<>());

        Set<InsurancePackageItem> packageItems = new HashSet<>();

        if (dto.getPackageItems() != null && !dto.getPackageItems().isEmpty()) {
            int displayOrder = 1;

            for (InsurancePackageItemDto itemDto : dto.getPackageItems()) {
                InsurancePackageItem item = null;
                if (itemDto.getId() != null)
                    item = insurancePackageItemRepository.findById(itemDto.getId()).orElse(null);
                if (item == null) {
                    item = new InsurancePackageItem();
                    if (itemDto.getId() != null)
                        item.setId(itemDto.getId());
                }

                item.setDisplayOrder(displayOrder);
                item.setDescription(itemDto.getDescription());
                item.setName(itemDto.getName());

                item.setInsurancePackage(entity);

                packageItems.add(item);

                displayOrder++;
            }
        }

        entity.getPackageItems().clear();
        entity.getPackageItems().addAll(packageItems);
    }
}
