package com.globits.hr.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.StaffCivilServantCategoryGrade;
import com.globits.hr.dto.StaffCivilServantGradeDto;
import com.globits.hr.repository.StaffCivilServantGradeRepository;
import com.globits.hr.service.StaffCivilServantGradeService;
import com.globits.security.domain.User;

@Transactional
@Service
public class StaffCivilServantGradeServiceImpl extends GenericServiceImpl<StaffCivilServantCategoryGrade, UUID> implements StaffCivilServantGradeService {
    @Autowired
    private StaffCivilServantGradeRepository staffCivilServantGradeRepository;

    @Override
    public StaffCivilServantGradeDto saveStaffCivilServantGrade(StaffCivilServantGradeDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User modifiedUser;
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        if (authentication != null) {
            modifiedUser = (User) authentication.getPrincipal();
            currentUserName = modifiedUser.getUsername();
        }

        StaffCivilServantCategoryGrade staffCivilServantGrade = null;
        if (dto.getId() != null) {
            staffCivilServantGrade = this.findById(dto.getId());
        }
        if (staffCivilServantGrade == null) {
            staffCivilServantGrade = new StaffCivilServantCategoryGrade();
            staffCivilServantGrade.setCreateDate(currentDate);
            staffCivilServantGrade.setCreatedBy(currentUserName);
        }
        staffCivilServantGrade.setModifiedBy(currentUserName);
        staffCivilServantGrade.setModifyDate(currentDate);

        staffCivilServantGrade.setIsCurrent(dto.getIsCurrent());
        staffCivilServantGrade.setFromDate(dto.getFromDate());
        staffCivilServantGrade.setToDate(dto.getToDate());
        staffCivilServantGrade.setStaff(dto.getStaff());
        staffCivilServantGrade.setCivilServantCategoryGrade(dto.getCivilServantCategoryGrade());
        staffCivilServantGrade = staffCivilServantGradeRepository.save(staffCivilServantGrade);
        return new StaffCivilServantGradeDto(staffCivilServantGrade);
    }

    @Override
    public StaffCivilServantGradeDto getStaffCivilServantGrade(UUID id) {
        StaffCivilServantCategoryGrade entity = this.findById(id);
        if (entity == null) {
            return null;
        }
        return new StaffCivilServantGradeDto(entity);
    }

    @Override
    public Page<StaffCivilServantGradeDto> getPage(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<StaffCivilServantCategoryGrade> _page = staffCivilServantGradeRepository.findAll(pageable);

        List<StaffCivilServantCategoryGrade> _content = _page.getContent();
        List<StaffCivilServantGradeDto> content = new ArrayList<>();

        for (StaffCivilServantCategoryGrade entity : _content) {
            StaffCivilServantGradeDto dto = new StaffCivilServantGradeDto(entity);
            content.add(dto);
        }

        return new PageImpl<>(content, pageable, _page.getTotalElements());
    }

    @Override
    public Boolean removeStaffCivilServantGrade(UUID id) {
        StaffCivilServantCategoryGrade staffCivilServantGrade = this.findById(id);
        if (staffCivilServantGrade != null) {
            staffCivilServantGradeRepository.delete(staffCivilServantGrade);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean deleteMultiple(StaffCivilServantGradeDto[] dtos) {
        boolean ret = true;
        if (dtos == null || dtos.length <= 0) {
            return ret;
        }
        ArrayList<StaffCivilServantCategoryGrade> staffCivilServantGrades = new ArrayList<>();
        for (StaffCivilServantGradeDto dto : dtos) {
            StaffCivilServantCategoryGrade entity = this.findById(dto.getId());
            if (entity == null) {
                throw new RuntimeException();
            }
            staffCivilServantGrades.add(entity);
        }
        staffCivilServantGradeRepository.deleteAllInBatch(staffCivilServantGrades);
        return ret;
    }
}
