package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.HrEducationLevel;
import com.globits.hr.dto.CertificateDto;

@Repository
public interface HrEducationLevelRepository extends JpaRepository<HrEducationLevel, UUID> {
	@Query("select el from HrEducationLevel el where el.code = ?1")
	List<HrEducationLevel> findByCode(String code);

	@Query("select count(entity.id) from Certificate entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
	Long checkCode(String code, UUID id);

	@Query("select new com.globits.hr.dto.CertificateDto(ed) from Certificate ed")
	Page<CertificateDto> getListPage( Pageable pageable);
}
