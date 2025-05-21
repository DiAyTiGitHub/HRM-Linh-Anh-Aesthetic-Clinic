package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.salary.domain.*;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.SalaryResultItemDto;
import com.globits.salary.dto.SalaryResultItemGroupDto;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.SalaryResultItemGroupService;
import com.globits.salary.service.SalaryResultService;
import com.globits.salary.service.SalaryResultStaffItemService;
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
public class SalaryResultItemGroupServiceImpl extends GenericServiceImpl<SalaryResultItemGroup, UUID> implements SalaryResultItemGroupService {
    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryResultRepository salaryResultRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryResultStaffRepository salaryResultStaffRepository;

    @Autowired
    private SalaryResultStaffItemRepository salaryResultStaffItemRepository;

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private SalaryResultItemRepository salaryResultItemRepository;

    @Autowired
    private SalaryResultItemGroupRepository salaryResultItemGroupRepository;

    @Override
    public void copyFromSalaryTemplateItemGroup(SalaryResult result) {
        SalaryTemplate template = result.getSalaryTemplate();

        if (result.getResultItemGroups() == null) {
            result.setResultItemGroups(new HashSet<SalaryResultItemGroup>());
        }

        Set<SalaryResultItemGroup> resultGroups = new HashSet<SalaryResultItemGroup>();
        if (template.getTemplateItemGroups() != null && template.getTemplateItemGroups().size() > 0) {
            for (SalaryTemplateItemGroup templateGroup : template.getTemplateItemGroups()) {
                SalaryResultItemGroup resultGroup = null;
                List<SalaryResultItemGroup> availableGroups = salaryResultItemGroupRepository.getBySalaryResultIdAndTemplateItemGroupId(result.getId(), templateGroup.getId());

                if (availableGroups != null && availableGroups.size() > 0) {
                    resultGroup = availableGroups.get(0);
                }

                if (resultGroup == null) {
                    resultGroup = new SalaryResultItemGroup();
                    resultGroup.setCopiedTemplateItemGroup(templateGroup);
                    resultGroup.setSalaryResult(result);
                }

                resultGroup.setName(templateGroup.getName());
                resultGroup.setDescription(templateGroup.getDescription());

                resultGroup = salaryResultItemGroupRepository.save(resultGroup);

                resultGroups.add(resultGroup);
            }
        }

        result.getResultItemGroups().clear();
        result.getResultItemGroups().addAll(resultGroups);
    }

    @Override
    public void handleSetSalaryResultItemGroupsFromConfig(SalaryResult entity, SalaryResultDto dto) {
        if (entity.getResultItemGroups() == null)
            entity.setResultItemGroups(new HashSet<SalaryResultItemGroup>());

        Set<SalaryResultItemGroup> resultItemGroups = new HashSet<>();
        if (dto.getResultItemGroups() != null && dto.getResultItemGroups().size() > 0) {

            for (SalaryResultItemGroupDto groupDto : dto.getResultItemGroups()) {
                SalaryResultItemGroup group = null;
                if (groupDto.getId() != null)
                    group = salaryResultItemGroupRepository.findById(groupDto.getId()).orElse(null);
                if (group == null) {
                    group = new SalaryResultItemGroup();
                    if (groupDto.getId() != null) group.setId(groupDto.getId());
                }

                group.setDescription(groupDto.getDescription());
                group.setName(groupDto.getName());
                group.setSalaryResult(entity);

                if (group.getDescription() == null && group.getName() == null) continue;

                group = salaryResultItemGroupRepository.save(group);

                resultItemGroups.add(group);
            }
        }

        entity.getResultItemGroups().clear();
        entity.getResultItemGroups().addAll(resultItemGroups);
    }
}
