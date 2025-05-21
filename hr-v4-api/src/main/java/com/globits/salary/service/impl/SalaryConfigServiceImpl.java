package com.globits.salary.service.impl;

import com.globits.core.domain.Department;
import com.globits.core.repository.DepartmentRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.HRDepartmentRepository;
import com.globits.salary.domain.*;
import com.globits.salary.dto.SalaryConfigDepartmentDto;
import com.globits.salary.dto.SalaryConfigDto;
import com.globits.salary.dto.SalaryConfigItemDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.SalaryConfigService;
import com.globits.security.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class SalaryConfigServiceImpl extends GenericServiceImpl<SalaryConfig, UUID> implements SalaryConfigService {
    @Autowired
    SalaryConfigRepository salaryConfigRepository;

    @Autowired
    SalaryConfigItemRepository salaryConfigItemRepository;

    @Autowired
    SalaryItemRepository salaryItemRepository;

    @Autowired
    SalaryTypeRepository salaryTypeRepository;
    @Autowired
    SalaryConfigDepartmentRepository salaryConfigDepartmentRepository;
    @Autowired
    HRDepartmentRepository hrDepartmentRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    @Modifying
    @Transactional
    public SalaryConfigDto saveSalaryConfig(SalaryConfigDto dto) {

        SalaryConfig salaryConfig = null;
        if (dto != null) {
            if (dto.getId() != null)
                salaryConfig = this.findById(dto.getId());
            if (salaryConfig == null) {
                salaryConfig = new SalaryConfig();
            }

            if (dto.getCode() != null) {
                if (checkCode(dto.getId(), dto.getCode())) return null;
                dto.setCode(dto.getCode());
            }

            // if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            //     salaryConfig.setDepartment(departmentRepository.getOne(dto.getDepartment().getId()));
            // }

            salaryConfig.setName(dto.getName());
            salaryConfig.setOtherName(dto.getOtherName());
            salaryConfig.setDefaultValue(dto.getDefaultValue());
            salaryConfig.setDescription(dto.getDescription());
            salaryConfig.setCode(dto.getCode());
            salaryConfig.setVoided(dto.getVoided());

            // add salary type
            if (dto.getSalaryType() != null && dto.getSalaryType().getId() != null) {
                SalaryType salaryType = salaryTypeRepository
                        .findById(dto.getSalaryType().getId()).orElse(null);
                if (salaryType == null) return null;
                salaryConfig.setSalaryType(salaryType);
            } else {
                salaryConfig.setSalaryType(null);
            }

            if (dto.getSalaryConfigItems() != null && dto.getSalaryConfigItems().size() > 0) {
                HashSet<SalaryConfigItem> salaryConfigItems = new HashSet<>();
                for (SalaryConfigItemDto sDto : dto.getSalaryConfigItems()) {
                    SalaryConfigItem salaryConfigItem = null;
                    if (sDto.getId() != null) {
                        Optional<SalaryConfigItem> optional = salaryConfigItemRepository.findById(sDto.getId());
                        if (optional.isPresent()) {
                            salaryConfigItem = optional.get();
                        }
                    }
                    if (salaryConfigItem == null) {
                        salaryConfigItem = new SalaryConfigItem();
                    }
                    salaryConfigItem.setSalaryConfig(salaryConfig);
                    if (sDto.getSalaryItem() != null && sDto.getSalaryItem().getId() != null) {
                        SalaryItem salaryItem = salaryItemRepository.getOne(sDto.getSalaryItem().getId());
                        salaryConfigItem.setSalaryItem(salaryItem);
                    }
                    salaryConfigItem.setFormula(sDto.getFormula());
                    salaryConfigItem.setItemOrder(sDto.getItemOrder());
                    salaryConfigItems.add(salaryConfigItem);
                }
                if (salaryConfig.getSalaryConfigItems() != null) {
                    salaryConfig.getSalaryConfigItems().clear();
                    salaryConfig.getSalaryConfigItems().addAll(salaryConfigItems);
                } else {
                    salaryConfig.setSalaryConfigItems(null);
                }
            }
            //save config salary for departments
            Set<SalaryConfigDepartment> departments = new HashSet<>();
            if (dto.getDepartments() != null && !dto.getDepartments().isEmpty()) {
                for (HRDepartmentDto department : dto.getDepartments()) {
                    if (department == null || department.getId() == null) continue;

                    List<SalaryConfigDepartment> existedRelationships = salaryConfigDepartmentRepository
                            .findByDepartmentIdAndSalaryConfigId(department.getId(), salaryConfig.getId());
                    SalaryConfigDepartment salaryConfigRelationDepartment = null;

                    if (existedRelationships != null && !existedRelationships.isEmpty()) {
                        // Relationship already exists
                        salaryConfigRelationDepartment = existedRelationships.get(0);
                    } else {
                        // Relationship does NOT EXIST => CREATE NEW
                        salaryConfigRelationDepartment = new SalaryConfigDepartment();
                        HRDepartment currentDepartment = hrDepartmentRepository.findById(department.getId()).orElse(null);
                        if (currentDepartment == null) {
                            continue; // Skip to the next iteration if currentDepartment is null
                        }
                        salaryConfigRelationDepartment.setDepartment(currentDepartment);
                        salaryConfigRelationDepartment.setSalaryConfig(salaryConfig);
                    }
                    departments.add(salaryConfigRelationDepartment);
                }
            }
            if (salaryConfig.getDepartments() == null) salaryConfig.setDepartments(departments);
            else {
                salaryConfig.getDepartments().clear();
                salaryConfig.getDepartments().addAll(departments);
            }
            salaryConfig = salaryConfigRepository.save(salaryConfig);
            return new SalaryConfigDto(salaryConfig, true);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteSalaryConfig(UUID id) {
        if (id == null) return null;
        SalaryConfig salaryConfig = salaryConfigRepository.findById(id).orElse(null);
        if (salaryConfig == null) return false;
        salaryConfigRepository.delete(salaryConfig);
        return true;
    }

    @Override
    public SalaryConfigDto getSalaryConfig(UUID id) {
        if (id == null) {
            return null;
        }

        SalaryConfig salaryConfig = salaryConfigRepository.findById(id).orElse(null);

        if (salaryConfig == null) {
            return null;
        }

        return new SalaryConfigDto(salaryConfig, true);
    }


    @Override
    @Modifying
    @Transactional
    public Boolean deleteSalaryConfig(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return false;
        boolean isValid = true;
        for (UUID id : ids) {
            Boolean statusDelete = deleteSalaryConfig(id);
            if (!statusDelete) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<SalaryConfigDto> searchByPage(SearchDto dto) {
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

        String sqlCount = "select count(entity.id) from SalaryConfig as entity where (1=1) ";
        String sql = "select new  com.globits.salary.dto.SalaryConfigDto(entity,false ) from SalaryConfig as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text  ) ";
        }


        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, SalaryConfigDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<SalaryConfigDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = salaryConfigRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }


}
