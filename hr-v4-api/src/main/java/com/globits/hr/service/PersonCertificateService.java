package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.PersonCertificate;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.PersonCertificateDto;
import com.globits.hr.dto.search.PersonBankAccountSearchDto;
import com.globits.hr.dto.search.SearchPersonCertificateDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface PersonCertificateService extends GenericService<PersonCertificate, UUID> {
    PersonCertificateDto saveImportStaffEducationHistory(PersonCertificateDto dto);

    Page<PersonCertificateDto> searchByPage(SearchPersonCertificateDto dto);

    SearchPersonCertificateDto getInitialFilter();

    PersonCertificateDto saveOrUpdate(PersonCertificateDto dto);

    PersonCertificateDto getById(UUID id);

    Boolean deleteById(UUID id);

    Integer deleteMultiple(List<UUID> ids);

    List<PersonCertificateDto> getPersonCertificateAllByPersonId(UUID personId);

}
