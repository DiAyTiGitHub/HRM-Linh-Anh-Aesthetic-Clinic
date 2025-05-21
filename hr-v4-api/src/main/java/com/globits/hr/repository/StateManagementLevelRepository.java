package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.globits.hr.domain.StateManagementLevel;
import com.globits.hr.dto.StateManagementLevelDto;
public interface StateManagementLevelRepository extends JpaRepository<StateManagementLevel, UUID>{
	@Query("select new com.globits.hr.dto.StateManagementLevelDto(s) from StateManagementLevel s")
    Page<StateManagementLevelDto> getListPage(Pageable pageable);

    @Query("select p from StateManagementLevel p where p.code = ?1")
    List<StateManagementLevel> findByCode(String code);

    @Query("select count(entity.id) from StateManagementLevel entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);
    
    @Query("select new com.globits.hr.dto.StateManagementLevelDto(ed) from StateManagementLevel ed where ed.name like ?1 or ed.code like ?2")
    Page<StateManagementLevelDto> searchByPage(String name, String code, Pageable pageable);
    
    @Query("select entity from StateManagementLevel entity where entity.name =?1")
    List<StateManagementLevel> findByName(String name);
    
    @Query("select count(p.id) from StateManagementLevel p where p.code = ?1 and (p.id <> ?2 or ?2 is null) ")
    Long countByCode(String code, UUID id);

}
