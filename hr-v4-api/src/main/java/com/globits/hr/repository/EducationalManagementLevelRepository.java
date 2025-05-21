package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.globits.hr.domain.EducationalManagementLevel;
import com.globits.hr.dto.EducationalManagementLevelDto;

public interface EducationalManagementLevelRepository extends JpaRepository<EducationalManagementLevel, UUID> {
	@Query("select new com.globits.hr.dto.EducationalManagementLevelDto(s) from EducationalManagementLevel s")
    Page<EducationalManagementLevelDto> getListPage(Pageable pageable);

    @Query("select p from EducationalManagementLevel p where p.code = ?1")
    List<EducationalManagementLevel> findByCode(String code);

    @Query("select count(entity.id) from EducationalManagementLevel entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);
    
    @Query("select new com.globits.hr.dto.EducationalManagementLevelDto(ed) from EducationalManagementLevel ed where ed.name like ?1 or ed.code like ?2")
    Page<EducationalManagementLevelDto> searchByPage(String name, String code, Pageable pageable);
    
    @Query("select entity from EducationalManagementLevel entity where entity.name =?1")
    List<EducationalManagementLevel> findByName(String name);
    
    @Query("select count(p.id) from EducationalManagementLevel p where p.code = ?1 and (p.id <> ?2 or ?2 is null) ")
    Long countByCode(String code, UUID id);
    
}
