package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffAllowanceHistory;
import com.globits.hr.dto.StaffAllowanceHistoryDto;
import com.globits.hr.repository.AllowanceTypeRepository;
import com.globits.hr.repository.StaffAllowanceHistoryRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffAllowanceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffAllowanceHistoryServiceImpl extends GenericServiceImpl<StaffAllowanceHistory , UUID> implements StaffAllowanceHistoryService {
    @Autowired
    StaffAllowanceHistoryRepository staffAllowanceHistoryRepository;
    @Autowired
    AllowanceTypeRepository allowanceTypeRepository;
    @Autowired
    StaffRepository staffRepository;
    @Override
    public Page<StaffAllowanceHistoryDto> getPage(int pageIndex, int pageSize){
        if (pageIndex >1){
            pageIndex--;
        }else{
            pageIndex = 0;
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return this.staffAllowanceHistoryRepository.getPages(pageable);
    }
    @Override
    public List<StaffAllowanceHistoryDto> getAll(UUID id){
        return this.staffAllowanceHistoryRepository.getAll(id);
    }
    @Override
    public StaffAllowanceHistoryDto getStaffAllowanceHistoryById(UUID id){
        return  this.staffAllowanceHistoryRepository.getStaffAllowanceHistoryById(id);
    }
    @Override
    public StaffAllowanceHistoryDto saveStaffAllowanceHistory(StaffAllowanceHistoryDto dto , UUID id) {
        Staff staff = null;
        if (dto != null && dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = this.staffRepository.getOne(dto.getStaff().getId());
        }
        StaffAllowanceHistory staffAllowanceHistory = new StaffAllowanceHistory();
        if (id != null) {
            Optional<StaffAllowanceHistory> optional = staffAllowanceHistoryRepository.findById(id);
            if (optional.isPresent()) {
                staffAllowanceHistory = optional.get();
            }
        } else {
            if (dto != null &&dto.getId() != null) {
                Optional<StaffAllowanceHistory> optional = staffAllowanceHistoryRepository.findById(dto.getId());
                if (optional.isPresent()) {
                    staffAllowanceHistory = optional.get();
                }
            }
        }
        if (dto != null){
            if (dto.getCoefficient() != null){
                staffAllowanceHistory.setCoefficient(dto.getCoefficient());
            }
            if (dto.getNote()!= null){
                staffAllowanceHistory.setNote(dto.getNote());
            }
            if (dto.getStartDate()!= null){
                staffAllowanceHistory.setStartDate(dto.getStartDate());
            }
            if (dto.getEndDate()!= null){
                staffAllowanceHistory.setEndDate(dto.getEndDate());
            }
        }
        staffAllowanceHistory.setStaff(staff);
        staffAllowanceHistory = this.staffAllowanceHistoryRepository.save(staffAllowanceHistory);
        if (dto != null){
            dto.setId(staffAllowanceHistory.getId());
        }
        return dto;
    }
    @Override
    public Boolean removeLists(List<UUID> ids) {
        if (ids != null && ids.size() > 0) {
            for (UUID id : ids) {
                this.staffAllowanceHistoryRepository.deleteById(id);
            }
        }
        return false;

    }

    @Override
    public StaffAllowanceHistoryDto removeStaffAllowanceHistory(UUID id) {
        StaffAllowanceHistoryDto staffAllowanceHistoryDto = new StaffAllowanceHistoryDto(this.staffAllowanceHistoryRepository.getOne(id));
        if (staffAllowanceHistoryRepository != null) {
            this.staffAllowanceHistoryRepository.deleteById(id);
        }
        return staffAllowanceHistoryDto;
    }
}
