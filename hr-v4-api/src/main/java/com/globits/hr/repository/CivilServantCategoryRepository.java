package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.CivilServantCategory;
import com.globits.hr.dto.CivilServantCategoryDto;

@Repository
public interface CivilServantCategoryRepository extends JpaRepository<CivilServantCategory, UUID> {
    @Query("select count(entity.id) from CivilServantCategory entity where entity.code =?2 and (entity.id <> ?1 or ?1 is null) ")
    Long checkCode(UUID id, String code);
    
    @Query("select count(entity.id) from CivilServantCategory entity where entity.name =?2 and (entity.id <> ?1 or ?1 is null) ")
    Long checkName(UUID id, String name);

    @Query("select c FROM CivilServantCategory c where c.code = ?1 ")
    List<CivilServantCategory> findByCode(String name);

    @Query("select new com.globits.hr.dto.CivilServantCategoryDto(c) FROM CivilServantCategory c ")
    List<CivilServantCategoryDto> getAllCivilServantCategory();
}

