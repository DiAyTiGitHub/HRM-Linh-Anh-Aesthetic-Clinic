package com.globits.hr.service.impl;

import com.globits.core.repository.CountryRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffOverseasWorkHistory;
import com.globits.hr.dto.StaffOverseasWorkHistoryDto;
import com.globits.hr.repository.StaffOverseasWorkHistoryRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffOverseasWorkHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class StaffOverseasWorkHistoryServiceImpl extends GenericServiceImpl<StaffOverseasWorkHistory, UUID> implements StaffOverseasWorkHistoryService {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    StaffOverseasWorkHistoryRepository staffOverseasWorkHistoryRepository;
    @Autowired
    StaffRepository staffRepository;
    @Override
    public Page<StaffOverseasWorkHistoryDto> getPage(int pageIndex, int pageSize){
        if (pageIndex >1){
            pageIndex--;
        }else{
            pageIndex = 0;
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return this.staffOverseasWorkHistoryRepository.getPages(pageable);
    }

    public List<StaffOverseasWorkHistoryDto> getAll(UUID id){
        return this.staffOverseasWorkHistoryRepository.getAll(id);
    }
    @Override
    public StaffOverseasWorkHistoryDto getStaffOverseasWorkHistoryById(UUID id){
        return  this.staffOverseasWorkHistoryRepository.getStaffOverseasWorkHistoryById(id);
    }
    @Override
    public StaffOverseasWorkHistoryDto saveStaffOverseasWorkHistory(StaffOverseasWorkHistoryDto dto , UUID id) {
        Staff staff = null;
        if (dto != null && dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = this.staffRepository.getOne(dto.getStaff().getId());
        }
        StaffOverseasWorkHistory staffOverseasWorkHistory = new StaffOverseasWorkHistory();
        if (id != null) {
            Optional<StaffOverseasWorkHistory> optional = staffOverseasWorkHistoryRepository.findById(id);
            if (optional.isPresent()) {
                staffOverseasWorkHistory = optional.get();
            }
        } else {
            if (dto != null &&dto.getId() != null) {
                Optional<StaffOverseasWorkHistory> optional = staffOverseasWorkHistoryRepository.findById(dto.getId());
                if (optional.isPresent()) {
                    staffOverseasWorkHistory = optional.get();
                }
            }
        }
        if (dto != null){
            if (dto.getDecisionNumber() != null){
                staffOverseasWorkHistory.setDecisionNumber(dto.getDecisionNumber());
            }
            if (dto.getPurpose()!= null){
                staffOverseasWorkHistory.setPurpose(dto.getPurpose());
            }
        }
        staffOverseasWorkHistory.setStaff(staff);
        staffOverseasWorkHistory = this.staffOverseasWorkHistoryRepository.save(staffOverseasWorkHistory);
        if (dto != null){
            dto.setId(staffOverseasWorkHistory.getId());
        }
        return dto;
    }
    @Override
    public Boolean removeLists(List<UUID> ids) {
        if (ids != null && ids.size() > 0) {
            for (UUID id : ids) {
                this.staffOverseasWorkHistoryRepository.deleteById(id);
            }
        }
        return false;

    }

    @Override
    public StaffOverseasWorkHistoryDto removeStaffOverseasWorkHistory(UUID id) {
        StaffOverseasWorkHistoryDto staffOverseasWorkHistoryDto = new StaffOverseasWorkHistoryDto(this.staffOverseasWorkHistoryRepository.getOne(id));
        if (staffOverseasWorkHistoryRepository != null) {
            this.staffOverseasWorkHistoryRepository.deleteById(id);
        }
        return staffOverseasWorkHistoryDto;
    }
}