package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.CivilServantCategoryGrade;
import com.globits.hr.dto.CivilServantCategoryGradeDto;

@Repository
public interface CivilServantCategoryGradeRepository extends JpaRepository<CivilServantCategoryGrade, UUID> {
    @Query("select new com.globits.hr.dto.CivilServantCategoryGradeDto(s) from CivilServantCategoryGrade s")
    Page<CivilServantCategoryGradeDto> getListPage(Pageable pageable);

    @Query("select p from CivilServantCategoryGrade p where p.code = ?1")
    List<CivilServantCategoryGrade> findByCode(String code);

    @Query("select count(entity.id) from CivilServantCategoryGrade entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select new com.globits.hr.dto.CivilServantCategoryGradeDto(c) FROM CivilServantCategoryGrade c ")
    List<CivilServantCategoryGradeDto> getAllCivilServantCategoryGrade();
}
