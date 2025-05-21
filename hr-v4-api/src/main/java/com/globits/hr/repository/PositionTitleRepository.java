package com.globits.hr.repository;

import com.globits.hr.domain.PositionTitle;
import com.globits.hr.dto.DepartmentsTreeDto;
import com.globits.hr.dto.PositionTitleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PositionTitleRepository extends JpaRepository<PositionTitle, UUID> {
    @Query("select p from PositionTitle p where p.code = ?1 and (p.voided IS NULL or p.voided = false)")
    List<PositionTitle> findByCode(String code);

    @Query("select p from PositionTitle p where p.shortName = ?1")
    List<PositionTitle> findByShortName(String shortName);

    @Query("select count(entity.id) from PositionTitle entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select new  com.globits.hr.dto.DepartmentsTreeDto(entity) from Department entity where entity.parent is null")
    Page<DepartmentsTreeDto> getByRoot(Pageable pageable);

    @Query("select new com.globits.hr.dto.PositionTitleDto(p) from PositionTitle p ")
    List<PositionTitleDto> getListPositionTitle();

    @Query("select new com.globits.hr.dto.PositionTitleDto(p) from PositionTitle p ")
    Page<PositionTitleDto> getPage(Pageable pageable);

    @Query(value = "SELECT code FROM tbl_position_title WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);



}
