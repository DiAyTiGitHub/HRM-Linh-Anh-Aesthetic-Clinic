package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.CivilServantGrade;
import com.globits.hr.dto.CivilServantGradeDto;

@Repository
public interface CivilServantGradeRepository extends JpaRepository<CivilServantGrade, UUID> {
    @Query("select count(entity.id) from CivilServantGrade entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select new com.globits.hr.dto.CivilServantGradeDto(c) FROM CivilServantGrade c ")
    List<CivilServantGradeDto> getAllCivilServantGrade();

    @Query("select new com.globits.hr.dto.CivilServantGradeDto(c) FROM CivilServantGrade c ")
    Page<CivilServantGradeDto> getPage(Pageable pageable);
}
