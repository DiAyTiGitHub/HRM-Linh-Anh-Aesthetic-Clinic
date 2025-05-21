package com.globits.hr.service.impl;


import com.globits.core.domain.GlobalProperty;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.HrGlobalPropertyDto;
import com.globits.hr.repository.HrGlobalPropertyRepository;
import com.globits.hr.service.HrGlobalPropertyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class HrGlobalPropertyServiceImpl extends GenericServiceImpl<GlobalProperty, UUID> implements HrGlobalPropertyService {
    @Autowired
    HrGlobalPropertyRepository globalPropertyRepository;

    @Override
    public HrGlobalPropertyDto saveGlobalProperty(HrGlobalPropertyDto dto, String property) {
        if (dto != null) {
            String currentUserName = "Unknown User";
            GlobalProperty entity = null;
            if (property != null) {
                if (dto.getProperty() != null && !dto.getProperty().equals(property)) {
                    return null;
                }
                GlobalProperty globalProperty = globalPropertyRepository.findByProperty(property);
                if (globalProperty == null) {
                    return null;
                }
                entity = globalProperty;
                entity.setPropertyName(dto.getPropertyName());
                entity.setPropertyValue(dto.getPropertyValue());
                entity.setDescription(dto.getDescription());
                entity.setDataTypeName(dto.getDataTypeName());
            }
            if (entity == null) {
                entity = new GlobalProperty();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
                entity.setCreatedBy(currentUserName);
                entity.setModifiedBy(currentUserName);
                entity.setProperty(dto.getProperty());
                entity.setPropertyName(dto.getPropertyName());
                entity.setPropertyValue(dto.getPropertyValue());
                entity.setDescription(dto.getDescription());
                entity.setDataTypeName(dto.getDataTypeName());
            }
            entity = globalPropertyRepository.save(entity);
            return new HrGlobalPropertyDto(entity);
        }
        return null;
    }

    @Override
    public void remove(String property) {
        GlobalProperty entity = null;
        if (property != null) {
            entity = globalPropertyRepository.findByProperty(property);
        }
        if (entity != null) {
            globalPropertyRepository.delete(entity);
        }
    }

    @Override
    public List<HrGlobalPropertyDto> getList() {
        List<HrGlobalPropertyDto> globalPropertyList = globalPropertyRepository.getAll();
        return globalPropertyList;
    }

    @Override
    public HrGlobalPropertyDto findGlobalProperty(String property) {
        GlobalProperty globalProperty = null;
        if (property != null) {
            globalProperty = globalPropertyRepository.findByProperty(property);
        }
        if (globalProperty == null) {
            return null;
        }
        return new HrGlobalPropertyDto(globalProperty);
    }
}
