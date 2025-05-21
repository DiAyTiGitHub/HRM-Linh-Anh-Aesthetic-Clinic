package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.SalaryTemplateItemDto;
import com.globits.salary.dto.SalaryTemplateItemGroupDto;
import com.globits.salary.repository.SalaryItemRepository;
import com.globits.salary.repository.SalaryTemplateItemGroupRepository;
import com.globits.salary.repository.SalaryTemplateItemRepository;
import com.globits.salary.repository.SalaryTemplateRepository;
import com.globits.salary.service.SalaryTemplateItemGroupService;
import com.globits.salary.service.SalaryTemplateService;
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

@Transactional
@Service
public class SalaryTemplateItemGroupServiceImpl extends GenericServiceImpl<SalaryTemplateItemGroup, UUID>
        implements SalaryTemplateItemGroupService {

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryTemplateItemGroupRepository salaryTemplateItemGroupRepository;

    @Override
    public void handleSetSalaryTemplateItemGroups(SalaryTemplate entity, SalaryTemplateDto dto) {
        if (entity.getTemplateItemGroups() == null)
            entity.setTemplateItemGroups(new HashSet<SalaryTemplateItemGroup>());

        Set<SalaryTemplateItemGroup> templateItemGroups = new HashSet<>();
        if (dto.getTemplateItemGroups() != null && dto.getTemplateItemGroups().size() > 0) {

            for (SalaryTemplateItemGroupDto groupDto : dto.getTemplateItemGroups()) {
                SalaryTemplateItemGroup group = null;
                if (groupDto.getId() != null)
                    group = salaryTemplateItemGroupRepository.findById(groupDto.getId()).orElse(null);
                if (group == null) {
                    group = new SalaryTemplateItemGroup();
                    if (groupDto.getId() != null) group.setId(groupDto.getId());
                }

                group.setDescription(groupDto.getDescription());
                group.setName(groupDto.getName());
                group.setSalaryTemplate(entity);

                if (group.getDescription() == null && group.getName() == null) continue;

                group = salaryTemplateItemGroupRepository.save(group);

                templateItemGroups.add(group);
            }
        }

        entity.getTemplateItemGroups().clear();
        entity.getTemplateItemGroups().addAll(templateItemGroups);
    }
}
