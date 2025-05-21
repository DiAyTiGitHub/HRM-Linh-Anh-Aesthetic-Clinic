package com.globits.hr.service.impl;

import com.globits.core.repository.CountryRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffTrainingHistory;
import com.globits.hr.dto.StaffTrainingHistoryDto;
import com.globits.hr.repository.CertificateRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffTrainingHistoryRepository;
import com.globits.hr.service.StaffTrainingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffTrainingHistoryServiceImpl extends GenericServiceImpl<StaffTrainingHistory , UUID> implements StaffTrainingHistoryService {
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    StaffTrainingHistoryRepository staffTrainingHistoryRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CertificateRepository certificateRepository;
    @Override
    public Page<StaffTrainingHistoryDto> getPage(int pageIndex , int pageSize){
        if (pageIndex >1){
            pageIndex--;
        }else{
            pageIndex = 0;
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return this.staffTrainingHistoryRepository.getPages(pageable);
    }
    @Override
    public List<StaffTrainingHistoryDto> getAll(UUID id){
        return this.staffTrainingHistoryRepository.getAll(id);
    }
    @Override
    public StaffTrainingHistoryDto getStaffTrainingHistoryById(UUID id){
        return  this.staffTrainingHistoryRepository.getStaffTrainingHistoryById(id);
    }
    @Override
    public StaffTrainingHistoryDto saveStaffTrainingHistory(StaffTrainingHistoryDto dto , UUID id) {
        Staff staff = null;
        if (dto != null && dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = this.staffRepository.getOne(dto.getStaff().getId());
        }
        StaffTrainingHistory staffTrainingHistory = new StaffTrainingHistory();
        if (id != null) {
            Optional<StaffTrainingHistory> optional = staffTrainingHistoryRepository.findById(id);
            if (optional.isPresent()) {
                staffTrainingHistory = optional.get();
            }
        } else {
            if (dto != null &&dto.getId() != null) {
                Optional<StaffTrainingHistory> optional = staffTrainingHistoryRepository.findById(dto.getId());
                if (optional.isPresent()) {
                    staffTrainingHistory = optional.get();
                }
            }
        }
        if (dto != null){
            if (dto.getTrainingContent() != null){
                staffTrainingHistory.setTrainingContent(dto.getTrainingContent());
            }
            if (dto.getTrainingPlace()!= null){
                staffTrainingHistory.setTrainingPlace(dto.getTrainingPlace());
            }
            if (dto.getStartDate() != null){
                staffTrainingHistory.setStartDate(dto.getStartDate());
            }
            if (dto.getEndDate() != null){
                staffTrainingHistory.setEndDate(dto.getEndDate());
            }
        }
        staffTrainingHistory.setStaff(staff);
        staffTrainingHistory = this.staffTrainingHistoryRepository.save(staffTrainingHistory);
        if (dto != null){
            dto.setId(staffTrainingHistory.getId());
        }
        return dto;
    }
    @Override
    public Boolean removeLists(List<UUID> ids) {
        if (ids != null && ids.size() > 0) {
            for (UUID id : ids) {
                this.staffTrainingHistoryRepository.deleteById(id);
            }
        }
        return false;

    }

    @Override
    public StaffTrainingHistoryDto removeStaffTrainingHistory(UUID id) {
        StaffTrainingHistoryDto staffTrainingHistoryDto = new StaffTrainingHistoryDto(this.staffTrainingHistoryRepository.getOne(id));
        if (staffTrainingHistoryRepository != null) {
            this.staffTrainingHistoryRepository.deleteById(id);
        }
        return staffTrainingHistoryDto;
    }
}
