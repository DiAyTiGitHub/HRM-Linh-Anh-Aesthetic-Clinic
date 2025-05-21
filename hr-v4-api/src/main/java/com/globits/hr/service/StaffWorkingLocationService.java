package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.HrDocumentTemplate;
import com.globits.hr.domain.StaffWorkingLocation;
import com.globits.hr.dto.*;
import com.globits.hr.dto.importExcel.StaffWorkingLocationImport;
import com.globits.hr.dto.search.PersonBankAccountSearchDto;
import com.globits.hr.dto.search.SearchDto;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface StaffWorkingLocationService extends GenericService<StaffWorkingLocation, UUID> {
    HashMap<UUID, StaffWorkingLocationDto> getMainWorkingLocationMap();

    Page<StaffWorkingLocationDto> searchByPage(SearchDto dto);

    StaffWorkingLocationDto getById(UUID id);

    StaffWorkingLocationDto saveOrUpdate(StaffWorkingLocationDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    List<StaffWorkingLocationDto> saveStaffWorkingLocationImportFromExcel(List<StaffWorkingLocationImport> staffWorkingLocationImports);

    List<StaffWorkingLocationImport> importExcelStaffWorkingLocation(Sheet staffWorkingLocationSheet);
}
