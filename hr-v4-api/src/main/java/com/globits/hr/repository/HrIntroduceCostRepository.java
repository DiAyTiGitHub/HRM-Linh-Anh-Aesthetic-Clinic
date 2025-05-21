package com.globits.hr.repository;

import com.globits.hr.domain.HrIntroduceCost;
import com.globits.hr.domain.StaffDocumentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HrIntroduceCostRepository extends JpaRepository<HrIntroduceCost, UUID> {
//    @Query("select count(entity.id) from Certificate entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
//    Long checkCode(String code, UUID id);
//
//    @Query("select new com.globits.hr.dto.CertificateDto(ed) from Certificate ed")
//    Page<CertificateDto> getListPage(Pageable pageable);
//
//    @Query("select entity from Certificate entity where entity.name =?1")
//    List<Certificate> findByName(String name);
//
//    @Query("select entity from Certificate entity where entity.code =?1")
//    List<Certificate> findByCode(String code);
//
//    @Query("select entity from Certificate entity where entity.type =?1")
//    List<Certificate> findByType(Integer type);
}
