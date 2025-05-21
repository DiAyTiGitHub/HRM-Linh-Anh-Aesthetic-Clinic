package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffCivilServantCategoryGrade;
import com.globits.hr.dto.StaffCivilServantGradeDto;

public interface StaffCivilServantGradeService extends GenericService<StaffCivilServantCategoryGrade, UUID> {
    StaffCivilServantGradeDto saveStaffCivilServantGrade(StaffCivilServantGradeDto dto);

    StaffCivilServantGradeDto getStaffCivilServantGrade(UUID id);

    Page<StaffCivilServantGradeDto> getPage(int pageIndex, int pageSize);

    Boolean removeStaffCivilServantGrade(UUID long1);

    Boolean deleteMultiple(StaffCivilServantGradeDto[] dtos);
}
