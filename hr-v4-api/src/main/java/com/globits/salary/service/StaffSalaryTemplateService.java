package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchStaffSalaryTemplateDto;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.StaffSalaryTemplate;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.StaffSalaryTemplateDto;
import com.globits.salary.dto.search.CalculateSalaryRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface StaffSalaryTemplateService extends GenericService<StaffSalaryTemplate, UUID> {
    List<SalaryTemplate> getListValidSalaryTemplatesOfStaffInPeriod(CalculateSalaryRequest dto);

    StaffSalaryTemplateDto saveOrUpdate(StaffSalaryTemplateDto dto);

    StaffSalaryTemplateDto getById(UUID id);

    StaffSalaryTemplateDto findByStaffIdAndTemplateId(SearchStaffSalaryTemplateDto searchDto);

    UUID findStaffTemplateIdByStaffIdAndTemplateId(SearchStaffSalaryTemplateDto searchDto);

    Boolean deleteStaffSalaryTemplate(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    StaffSalaryTemplateDto updateStaffSalaryTemplate(StaffSalaryTemplateDto dto);

    Integer saveListStaffSalaryTemplate(StaffSalaryTemplateDto dto);

    Page<SalaryTemplateDto> getSalaryTemplatesOfStaff(CalculateSalaryRequest searchDto);

    Page<StaffSalaryTemplateDto> searchByPage(SearchStaffSalaryTemplateDto searchDto);

    List<StaffSalaryTemplate> findBySalaryTemplateIdAndRangeTime(SearchStaffSalaryTemplateDto searchDto);

    ByteArrayOutputStream exportImportResultStaffSalaryTemplate(MultipartFile file) throws IOException;

    SalaryTemplateDto getSalaryTemplate(SearchStaffSalaryTemplateDto dto);

}
