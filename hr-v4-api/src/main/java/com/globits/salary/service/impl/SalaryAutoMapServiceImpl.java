package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.HrConstants.SalaryAutoMapField;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.repository.SalaryAreaRepository;
import com.globits.salary.repository.SalaryAutoMapRepository;
import com.globits.salary.repository.SalaryItemRepository;
import com.globits.salary.service.SalaryAreaService;
import com.globits.salary.service.SalaryAutoMapService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class SalaryAutoMapServiceImpl extends GenericServiceImpl<SalaryAutoMap, UUID> implements SalaryAutoMapService {

    @Autowired
    private SalaryAutoMapRepository salaryAutoMapRepository;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Override
    public SalaryAutoMapDto saveOrUpdate(SalaryAutoMapDto dto) {
        if (dto == null || dto.getSalaryAutoMapField() == null) {
            return null;
        }

        SalaryAutoMap entity = null;
        if (dto.getId() != null) {
            entity = salaryAutoMapRepository.findById(dto.getId()).orElse(null);
        }

        if (entity == null) {
            entity = new SalaryAutoMap();
        }
        entity.setSalaryAutoMapField(dto.getSalaryAutoMapField());
        entity.setDescription(dto.getDescription());

        this.handleSetConfigSalaryItems(entity, dto);

        entity = salaryAutoMapRepository.save(entity);

        SalaryAutoMapDto result = new SalaryAutoMapDto(entity);

        return result;
    }

    private void handleSetConfigSalaryItems(SalaryAutoMap entity, SalaryAutoMapDto dto) {
        if (entity.getSalaryItems() == null)
            entity.setSalaryItems(new HashSet<>());

        Set<SalaryItem> salaryItems = new HashSet<>();
        if (dto.getSalaryItems() != null && !dto.getSalaryItems().isEmpty()) {

            for (SalaryItemDto itemDto : dto.getSalaryItems()) {
                SalaryItem item = salaryItemRepository.findById(itemDto.getId()).orElse(null);

                if (item == null) continue;

                item.setSalaryAutoMap(entity);

                salaryItems.add(item);
            }
        }

        entity.getSalaryItems().clear();
        entity.getSalaryItems().addAll(salaryItems);
    }

    @Override
    public SalaryAutoMapDto getById(UUID id) {
        SalaryAutoMap entity = salaryAutoMapRepository.findById(id).orElse(null);
        return new SalaryAutoMapDto(entity);
    }

    @Override
    public List<SalaryAutoMapDto> getAll(SearchDto searchDto) {
        return salaryAutoMapRepository.findAllSorted()
                .stream()
                .map(SalaryAutoMapDto::new)
                .collect(Collectors.toList());
    }


    @Override
    public List<SalaryItem> getCorrespondingSalaryItems(HrConstants.SalaryAutoMapField mapField) {
        List<SalaryAutoMap> listEntity = salaryAutoMapRepository.findByMapField(mapField.getValue());

        if (listEntity == null || listEntity.isEmpty()) {
            return new ArrayList<>();
        }

        SalaryAutoMap salaryAutoMap = listEntity.get(0);

        if (salaryAutoMap.getSalaryItems() == null || salaryAutoMap.getSalaryItems().isEmpty())
            return new ArrayList<>();

        return new ArrayList<>(salaryAutoMap.getSalaryItems());
    }

    @Override
    public List<String> getCorrespondingSalaryItemsCode(HrConstants.SalaryAutoMapField mapField) {
        List<SalaryItem> salaryItems = this.getCorrespondingSalaryItems(mapField);

        if (salaryItems == null || salaryItems.isEmpty()) return new ArrayList<>();

        List<String> itemCodes = new ArrayList<>();

        for (SalaryItem salaryItem : salaryItems) {
            itemCodes.add(salaryItem.getCode());
        }

        return itemCodes;
    }

    @Override
    public List<SalaryAutoMap> getBySalaryAutoMapField(HrConstants.SalaryAutoMapField mapField) {
        if (mapField == null) {
            return Collections.emptyList();
        }
        return salaryAutoMapRepository.findByMapField(mapField.getValue());

    }


    @Override
    public boolean isAutoMapConstants(String code) {
        if (code == null) return false;
        for (SalaryAutoMapField field : SalaryAutoMapField.values()) {
            if (field.getValue().equals(code)) {
                return true;
            }
        }
        return false;
    }

}
