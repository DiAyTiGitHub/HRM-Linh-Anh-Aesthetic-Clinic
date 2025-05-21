package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.HrSpeciality;
import com.globits.hr.dto.HrSpecialityDto;

@Repository
public interface HrSpecialityRepository extends JpaRepository<HrSpeciality, UUID> {
	
	@Query("select d from HrSpeciality d where d.code = ?1")
	List<HrSpeciality> findByCode(String code);
	
	@Query("select new com.globits.hr.dto.HrSpecialityDto(d) from HrSpeciality d")
	List<HrSpecialityDto> getListAllSpecialities();
	
	@Query("select new com.globits.hr.dto.HrSpecialityDto(d) from HrSpeciality d")
	Page<HrSpecialityDto> getListSpeciality(Pageable pageable);

	@Query(value = "SELECT code FROM tbl_hr_speciality WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
	String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
