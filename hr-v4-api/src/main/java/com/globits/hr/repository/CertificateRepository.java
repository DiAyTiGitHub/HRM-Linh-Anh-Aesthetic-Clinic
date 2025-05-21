package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.globits.hr.domain.Certificate;
import com.globits.hr.dto.CertificateDto;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    @Query("select count(entity.id) from Certificate entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select new com.globits.hr.dto.CertificateDto(ed) from Certificate ed")
    Page<CertificateDto> getListPage(Pageable pageable);

    @Query("select entity from Certificate entity where entity.name =?1")
    List<Certificate> findByName(String name);

    @Query("select entity from Certificate entity where entity.code =?1")
    List<Certificate> findByCode(String code);

    @Query("select entity from Certificate entity where entity.type =?1")
    List<Certificate> findByType(Integer type);
}
