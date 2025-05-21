package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.InsurancePackage;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffInsurancePackage;
import com.globits.hr.dto.InsurancePackageDto;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.StaffInsurancePackageDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.search.PersonBankAccountSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffInsurancePackageDto;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffInsurancePackageService extends GenericService<StaffInsurancePackage, UUID> {
    Page<StaffInsurancePackageDto> searchByPage(SearchStaffInsurancePackageDto dto);

    StaffInsurancePackageDto getById(UUID id);

    StaffInsurancePackageDto saveOrUpdate(StaffInsurancePackageDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);
}
