package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.PositionTitle;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.DepartmentsTreeDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.search.PositionTitleSearchDto;
import com.globits.hr.dto.search.SearchDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PositionTitleService extends GenericService<PositionTitle, UUID> {
    PositionTitleDto saveTitle(PositionTitleDto dto);

    PositionTitleDto getTitle(UUID id);

    Boolean removeTitle(UUID id);

    Boolean deleteMultiple(PositionTitleDto[] dtos);

    Boolean checkCode(UUID id, String code);

    Page<PositionTitleDto> searchByPage(PositionTitleSearchDto dto);

    Page<DepartmentsTreeDto> getByRoot(int pageIndex, int pageSize);

    PositionTitleDto saveOrUpdate(PositionTitleDto dto);

    Integer saveListPositionTitle(List<PositionTitleDto> dtos, Boolean isGroupPositionTitle);

    Boolean deleteByShortName(String shortName);

    Boolean isValidCode(PositionTitleDto dto);

    Workbook exportExcelPositionTitleData(PositionTitleSearchDto dto);

    Set<PositionTitle> getMainPositionByStaffId(UUID staffId);

    String autoGenerateCode(String configKey);
}
