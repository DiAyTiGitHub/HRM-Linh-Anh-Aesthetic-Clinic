package com.globits.salary.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.salary.domain.SalaryConfigItem;
import com.globits.salary.dto.SalaryConfigItemDto;
import com.globits.salary.repository.SalaryConfigItemRepository;
import com.globits.salary.repository.SalaryConfigRepository;
import com.globits.salary.service.SalaryConfigItemService;
import com.globits.security.domain.User;

@Transactional
@Service
public class SalaryConfigItemServiceImpl extends GenericServiceImpl<SalaryConfigItem, UUID> implements SalaryConfigItemService {
    @Autowired
    SalaryConfigRepository salaryConfigRepository;

    @Autowired
    SalaryConfigItemRepository salaryConfigItemRepository;

    @Override
    public Page<SalaryConfigItemDto> getPageBySalaryConfigId(UUID salaryConfigId, int pageSize, int pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return salaryConfigItemRepository.getPageBySalaryConfigId(salaryConfigId, pageable);
    }

    @Override
    public SalaryConfigItemDto getSalaryConfigItem(UUID id) {
        SalaryConfigItem salaryConfigItem = this.findById(id);
        if (salaryConfigItem != null) {
            return new SalaryConfigItemDto(salaryConfigItem);
        }
        return null;
    }

    @Override
    public SalaryConfigItemDto saveSalaryConfigItem(SalaryConfigItemDto dto) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        User modifiedUser = null;
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        if (authentication != null) {
            modifiedUser = (User) authentication.getPrincipal();
            currentUserName = modifiedUser.getUsername();
        }

        SalaryConfigItem salaryConfigItem = null;
        if (dto != null) {
            if (dto.getId() != null)
                salaryConfigItem = this.findById(dto.getId());
            if (salaryConfigItem == null) {//Nếu không tìm thấy thì tạo mới 1 đối tượng
                salaryConfigItem = new SalaryConfigItem();
                salaryConfigItem.setCreateDate(currentDate);
                salaryConfigItem.setCreatedBy(currentUserName);
            }
            salaryConfigItem.setItemOrder(dto.getItemOrder());
            salaryConfigItem.setFormula(dto.getFormula());

            salaryConfigItem.setModifyDate(currentDate);
            salaryConfigItem.setModifiedBy(currentUserName);

            salaryConfigItem = salaryConfigItemRepository.save(salaryConfigItem);
            return new SalaryConfigItemDto(salaryConfigItem);
        }
        return null;
    }
}
