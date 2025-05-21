package com.globits.hr.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.domain.Organization;
import com.globits.core.dto.OrganizationDto;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.repository.OrganizationRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.RewardFormDto;
import com.globits.hr.dto.StaffOverseasWorkHistoryDto;
import com.globits.hr.dto.StaffRewardHistoryDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.StaffRewardHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffRewardHistoryServiceImpl extends GenericServiceImpl<StaffRewardHistory, UUID> implements StaffRewardHistoryService {
    @Autowired
    private HrOrganizationRepository organizationRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private RewardFormRepository rewardFormRepository;
    @Autowired
    private StaffRewardHistoryRepository staffRewardHistoryRepository;
    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;
    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;

    @Override
    public StaffRewardHistoryDto saveStaffRewardHistory(StaffRewardHistoryDto dto, UUID id) {
        if (dto == null) {
            return null;
        }
        StaffRewardHistory entity = null;
        if (id != null) {
            Optional<StaffRewardHistory> optional = staffRewardHistoryRepository.findById(id);
            if (optional.isPresent()) {
                entity = optional.get();
            }
        } else {
            if (dto.getId() != null) {
                Optional<StaffRewardHistory> optional = staffRewardHistoryRepository.findById(dto.getId());
                if (optional.isPresent()) {
                    entity = optional.get();
                }
            }
        }
        if (entity == null) {
            entity = new StaffRewardHistory();
        }
        entity.setRewardDate(dto.getRewardDate());
        RewardForm rewardForm = null;
        if (dto.getRewardType() != null && dto.getRewardType().getId() != null) {
            rewardForm = rewardFormRepository.findById(dto.getRewardType().getId()).orElse(null);
        }
        entity.setRewardType(rewardForm);
        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = this.staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        if (staff == null) {
            return null;
        }
        entity.setStaff(staff);

        HrOrganization organization = null;
        if (dto.getOrganization() != null && dto.getOrganization().getId() != null) {
            organization = organizationRepository.findById(dto.getOrganization().getId()).orElse(null);
        }
        entity.setOrganization(organization);
        entity.setOrganizationName(dto.getOrganizationName());

        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            HRDepartment department = hrDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null);
            entity.setDepartment(department);
        } else {
            entity.setDepartment(null);
        }

        if (dto.getFile() != null && dto.getFile().getId() != null) {
            FileDescription file = fileDescriptionRepository.findById(dto.getFile().getId()).orElse(null);
            entity.setFile(file);
        } else {
            entity.setFile(null);
        }

        StaffRewardHistory response = this.staffRewardHistoryRepository.save(entity);

        return new StaffRewardHistoryDto(response);

    }

    @Override
    public Page<StaffRewardHistoryDto> getPage(int pageIndex, int pageSize) {
        if (pageIndex > 1) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return this.staffRewardHistoryRepository.getPages(pageable);
    }

    @Override
    public List<StaffRewardHistoryDto> getAll(UUID id) {
        return this.staffRewardHistoryRepository.getAll(id);
    }

    @Override
    public StaffRewardHistoryDto getStaffRewardHistoryById(UUID id) {
        return this.staffRewardHistoryRepository.getStaffRewardHistoryById(id);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean removeLists(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID id : ids) {
            StaffRewardHistoryDto result = this.removeStaffRewardHistory(id);
            if (result == null) isValid = false;
        }
        return isValid;
    }

    @Override
    public StaffRewardHistoryDto removeStaffRewardHistory(UUID id) {
        StaffRewardHistoryDto staffRewardHistoryDto = new StaffRewardHistoryDto(this.staffRewardHistoryRepository.getOne(id));
        if (staffRewardHistoryRepository != null) {
            this.staffRewardHistoryRepository.deleteById(id);
        }
        return staffRewardHistoryDto;
    }
}
