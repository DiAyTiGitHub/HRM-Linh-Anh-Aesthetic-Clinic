package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.AllowanceDto;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.dto.search.ChooseSalaryTemplateItemDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.SalaryTemplateItemConfigService;
import com.globits.salary.service.SalaryTemplateItemGroupService;
import com.globits.salary.service.SalaryTemplateItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Transactional
@Service
public class SalaryTemplateItemServiceImpl extends GenericServiceImpl<SalaryTemplateItem, UUID>
        implements SalaryTemplateItemService {

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryTemplateItemGroupRepository salaryTemplateItemGroupRepository;

    @Autowired
    private StaffSalaryItemValueRepository staffSalaryItemValueRepository;

    @Autowired
    private SalaryTemplateItemConfigRepository salaryTemplateItemConfigRepository;

    @Autowired
    private SalaryTemplateItemConfigService salaryTemplateItemConfigService;

    private SalaryTemplateItemDto createNewTemplateItemDto(UUID salaryTemplateId, UUID salaryItemId) {
        SalaryItem salaryItem = salaryItemRepository.findById(salaryItemId).orElse(null);
        if (salaryItem == null)
            return null;

        SalaryTemplateItemDto templateItem = new SalaryTemplateItemDto();

        templateItem.setId((new SalaryResultItem()).getId());
        templateItem.setSalaryTemplateId(salaryTemplateId);
        templateItem.setSalaryItem(new SalaryItemDto(salaryItem));
        templateItem.setDescription(salaryItem.getDescription());
        templateItem.setDisplayName(salaryItem.getName());

        templateItem.setCode(salaryItem.getCode());
        templateItem.setFormula(salaryItem.getFormula());
        templateItem.setType(salaryItem.getType());
        templateItem.setCalculationType(salaryItem.getCalculationType());
        templateItem.setIsTaxable(salaryItem.getIsTaxable());
        templateItem.setIsInsurable(salaryItem.getIsInsurable());
        templateItem.setMaxValue(salaryItem.getMaxValue());
        templateItem.setDefaultValue(salaryItem.getDefaultValue());
        templateItem.setValueType(salaryItem.getValueType());
        templateItem.setAllowance(new AllowanceDto(salaryItem.getAllowance()));

        templateItem.setUsingFormula(salaryItem.getFormula());

        return templateItem;
    }

    private SalaryTemplateItemDto getExistedResultItemInCurrentFEList(List<SalaryTemplateItemDto> currentTemplateItems,
                                                                      UUID findingItem) {
        if (currentTemplateItems == null || currentTemplateItems.isEmpty())
            return null;

        for (SalaryTemplateItemDto currentRI : currentTemplateItems) {
            if (currentRI.getSalaryItem() != null && currentRI.getSalaryItem().getId() != null
                    && currentRI.getSalaryItem().getId().equals(findingItem)) {
                return currentRI;
            }
        }

        return null;
    }

    private SalaryTemplateItemDto getExistedResultItemSavedInBE(UUID salaryTemplateId, UUID salaryItemId) {
        if (salaryItemId == null || salaryTemplateId == null)
            return null;

        List<SalaryTemplateItem> availableRIs = salaryTemplateItemRepository
                .getBySalaryTemplateIdAndSalaryItemId(salaryTemplateId, salaryItemId);
        if (availableRIs == null || availableRIs.isEmpty())
            return null;

        SalaryTemplateItemDto resultItem = new SalaryTemplateItemDto(availableRIs.get(0), true);
        return resultItem;
    }

    @Override
    public List<SalaryTemplateItemDto> handleChooseTemplateItems(ChooseSalaryTemplateItemDto dto) {
        List<SalaryTemplateItemDto> response = new ArrayList<>();

        // add to response the previous saved first, then add the new ones later
        List<UUID> unhandledItemIds = new ArrayList<>();
        for (UUID chosenItemId : dto.getChosenItemIds()) {
            SalaryTemplateItemDto chosenResultItem = null;

            // find in current result items in Frontend first
            chosenResultItem = getExistedResultItemInCurrentFEList(dto.getCurrentTemplateItems(), chosenItemId);

            if (chosenResultItem == null) {
                // find in current result items in Backend then
                chosenResultItem = getExistedResultItemSavedInBE(dto.getSalaryTemplateId(), chosenItemId);
            }

            if (chosenResultItem == null) {
                // mark that this salary item has this id is unhandled to response
                unhandledItemIds.add(chosenItemId);
                continue;
            }

            chosenResultItem.setDisplayOrder(response.size() + 1);
            response.add(chosenResultItem);
        }

        // add the remaining to response, these are new result items
        for (UUID chosenItemId : unhandledItemIds) {
            SalaryTemplateItemDto newResultItem = createNewTemplateItemDto(dto.getSalaryTemplateId(), chosenItemId);

            if (newResultItem == null)
                continue;

            newResultItem.setDisplayOrder(response.size() + 1);
            response.add(newResultItem);
        }

        Collections.sort(response, new Comparator<SalaryTemplateItemDto>() {
            @Override
            public int compare(SalaryTemplateItemDto o1, SalaryTemplateItemDto o2) {
                // First, compare by displayOrder
                if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
                    return 0;
                if (o1.getDisplayOrder() == null)
                    return 1;
                if (o2.getDisplayOrder() == null)
                    return -1;

                int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                if (orderComparison != 0) {
                    return orderComparison;
                }

                // If displayOrder is the same, compare by displayName (handling nulls)
                if (o1.getDisplayName() == null && o2.getDisplayName() == null)
                    return 0;
                if (o1.getDisplayName() == null)
                    return 1;
                if (o2.getDisplayName() == null)
                    return -1;
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        return response;
    }

    @Override
    public List<SalaryTemplateItemDto> getListSalaryTemplateItem(RequestSalaryValueDto dto) {
        List<SalaryTemplateItemDto> result = new ArrayList<>();

        List<UUID> ids = new ArrayList<>();
        SalaryTemplate salaryTemplate = null;
        if (dto.getSalaryTemplate() != null && dto.getSalaryTemplate().getId() != null) {
            salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        }
        if (salaryTemplate == null) {
            return null;
        }

        for (SalaryTemplateItem item : salaryTemplate.getTemplateItems()) {
            if (!dto.getGetAll()) {
                if (item.getSalaryItem().getCalculationType() == HrConstants.SalaryItemCalculationType.FIX.getValue()) {
                    ids.add(item.getSalaryItem().getId());
                    result.add(new SalaryTemplateItemDto(item));
                }
            } else {
                ids.add(item.getSalaryItem().getId());
                result.add(new SalaryTemplateItemDto(item));
            }
        }
        List<StaffSalaryItemValue> salaryItemValueList = staffSalaryItemValueRepository.findSalaryValuesBySalaryItemIdsAndStaffId(ids, dto.getStaff().getId());

        for (SalaryTemplateItemDto item : result) {
            for (StaffSalaryItemValue salaryItemValue : salaryItemValueList) {
                if (item.getSalaryItem().getId().equals(salaryItemValue.getSalaryItem().getId())) {
                    if (salaryItemValue.getValue() != null) {
                        item.setValue(salaryItemValue.getValue());
                    }
                }
            }
        }

        result.sort(Comparator.comparing(SalaryTemplateItemDto::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        return result;
    }

    @Override
    public SalaryTemplateItemDto saveSalaryTemplateItemWithSalaryTemplateItemConfig(SalaryTemplateItemDto dto) {
        if (dto == null || dto.getId() == null) return null;

        SalaryTemplateItem salaryTemplateItem = salaryTemplateItemRepository.findById(dto.getId()).orElse(null);
        if (salaryTemplateItem == null) {
            return null;
        }

        if (salaryTemplateItem.getTemplateItemConfigs() == null) {
            salaryTemplateItem.setTemplateItemConfigs(new HashSet<>());
        }

        salaryTemplateItem.getTemplateItemConfigs().clear();

        if (dto.getTemplateItemConfigs() != null && !dto.getTemplateItemConfigs().isEmpty()) {
            int compareOrder = 1;

            for (SalaryTemplateItemConfigDto itemConfig : dto.getTemplateItemConfigs()) {
                SalaryTemplateItemConfig salaryTemplateItemConfig = null;
                if (itemConfig.getId() != null) {
                    salaryTemplateItemConfig = salaryTemplateItemConfigRepository.findById(itemConfig.getId()).orElse(null);
                }
                if (salaryTemplateItemConfig == null) {
                    salaryTemplateItemConfig = new SalaryTemplateItemConfig();
                }

                salaryTemplateItemConfig.setTemplateItem(salaryTemplateItem);
                salaryTemplateItemConfig.setCompareOrder(compareOrder);

                // clear old item config value/formula depend on its config type
                salaryTemplateItemConfig.setItemValue(null);
                salaryTemplateItemConfig.setFormula(null);

                salaryTemplateItemConfig.setOperatorMinValue(itemConfig.getOperatorMinValue());
                salaryTemplateItemConfig.setMinValue(itemConfig.getMinValue());
                salaryTemplateItemConfig.setOperatorMaxValue(itemConfig.getOperatorMaxValue());
                salaryTemplateItemConfig.setMaxValue(itemConfig.getMaxValue());

                salaryTemplateItemConfig.setConfigType(itemConfig.getConfigType());

                if (salaryTemplateItemConfig.getConfigType() != null && salaryTemplateItemConfig.getConfigType().equals(HrConstants.ConfigType.FIX.getValue())) {
                    salaryTemplateItemConfig.setItemValue(itemConfig.getItemValue());
                } else if (salaryTemplateItemConfig.getConfigType() != null && salaryTemplateItemConfig.getConfigType().equals(HrConstants.ConfigType.USING_FORMULA.getValue())) {
                    salaryTemplateItemConfig.setFormula(itemConfig.getFormula());
                }

                salaryTemplateItem.getTemplateItemConfigs().add(salaryTemplateItemConfig);
            }
        }
        SalaryTemplateItem entity = salaryTemplateItemRepository.save(salaryTemplateItem);

        if (entity != null) {
            return new SalaryTemplateItemDto(entity);
        } else {
            return null;
        }
    }

    @Override
    public void handleSetSalaryTemplateItems(SalaryTemplate entity, SalaryTemplateDto dto) {
        if (entity.getTemplateItems() == null)
            entity.setTemplateItems(new HashSet<SalaryTemplateItem>());

        Set<SalaryTemplateItem> templateItems = new HashSet<>();
        if (dto.getTemplateItems() != null && !dto.getTemplateItems().isEmpty()) {

            for (SalaryTemplateItemDto itemDto : dto.getTemplateItems()) {
                SalaryTemplateItem item = null;
                if (itemDto.getId() != null)
                    item = salaryTemplateItemRepository.findById(itemDto.getId()).orElse(null);
                if (item == null) {
                    item = new SalaryTemplateItem();
                    if (itemDto.getId() != null)
                        item.setId(itemDto.getId());
                }

                item.setDisplayOrder(itemDto.getDisplayOrder());
                item.setDisplayName(itemDto.getDisplayName());
                item.setDescription(itemDto.getDescription());
                item.setFormula(itemDto.getFormula());

                item.setHiddenOnPayslip(itemDto.getHiddenOnPayslip());
                item.setHiddenOnSalaryBoard(itemDto.getHiddenOnSalaryBoard());

                String templateItemCode = itemDto.getCode();
                if (!StringUtils.hasText(templateItemCode)) continue;
                templateItemCode = templateItemCode.trim();

                item.setCode(templateItemCode);
                item.setType(itemDto.getType());
                item.setIsTaxable(itemDto.getIsTaxable());
                item.setIsInsurable(itemDto.getIsInsurable());
                item.setDefaultValue(itemDto.getDefaultValue());
                item.setMaxValue(itemDto.getMaxValue());
                item.setCalculationType(itemDto.getCalculationType());
                item.setValueType(itemDto.getValueType());
                item.setUsingFormula(itemDto.getUsingFormula());

                item.setSalaryTemplate(entity);

                if (itemDto.getTemplateItemGroupId() != null) {
                    SalaryTemplateItemGroup group = salaryTemplateItemGroupRepository
                            .findById(itemDto.getTemplateItemGroupId()).orElse(null);
                    item.setTemplateItemGroup(group);
                } else {
                    item.setTemplateItemGroup(null);
                }

                if (itemDto.getSalaryItem() != null) {
                    SalaryItem salaryItem = salaryItemRepository.findById(itemDto.getSalaryItem().getId()).orElse(null);
                    item.setSalaryItem(salaryItem);

                    if (salaryItem != null && salaryItem.getAllowance() != null) {
                        item.setAllowance(salaryItem.getAllowance());
                    }
                } else {
                    item.setSalaryItem(null);
                }

                if (item.getDisplayOrder() == null && item.getDisplayName() == null && item.getDescription() == null
                        && item.getSalaryItem() == null)
                    continue;

                salaryTemplateItemConfigService.handleSetToSalaryTemplateItems(item, itemDto);

                templateItems.add(item);
            }
        }

        entity.getTemplateItems().clear();
        entity.getTemplateItems().addAll(templateItems);
    }
}
