package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.globits.hr.domain.CivilServantType;
import com.globits.hr.dto.CivilServantTypeDto;

public interface CivilServantTypeRepository extends JpaRepository<CivilServantType, UUID> {
    @Query("select c FROM CivilServantType c where c.name = ?1 ")
    List<CivilServantType> findByName(String name);

    @Query("select count(entity.id) from CivilServantType entity where entity.code =?2 and (entity.id <> ?1 or ?1 is null )")
    Long checkCode(UUID id, String code);
    
    @Query("select count(entity.id) from CivilServantType entity where entity.name =?2 and (entity.id <> ?1 or ?1 is null )")
    Long checkName(UUID id, String name);

    @Query("select c FROM CivilServantType c where c.code = ?1")
    List<CivilServantType> findByCode(String code);

    @Query("select new com.globits.hr.dto.CivilServantTypeDto(c) FROM CivilServantType c ")
    List<CivilServantTypeDto> getAllCivilServantType();

    @Query("select new com.globits.hr.dto.CivilServantTypeDto(c) FROM CivilServantType c ")
    Page<CivilServantTypeDto> getPage(Pageable pageable);
}
