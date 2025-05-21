package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.globits.hr.dto.CertificateDto;
import com.globits.hr.dto.search.SearchDto;

@Service
public interface CertificateService {
    CertificateDto saveOrUpdate(UUID id, CertificateDto dto);

    Boolean delete(UUID id);

    CertificateDto getCertificate(UUID id);

    Page<CertificateDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);

    CertificateDto findByCode(String code);
}
