package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.globits.hr.domain.HRDiscipline;
import com.globits.hr.dto.HRDisciplineDto;

public interface HRDisciplineRepository extends JpaRepository<HRDiscipline, UUID> {
    @Query("select new com.globits.hr.dto.HRDisciplineDto(s) from HRDiscipline s")
    Page<HRDisciplineDto> getListPage(Pageable pageable);

    @Query("select c FROM HRDiscipline c where c.name = ?1 ")
    List<HRDiscipline> findByName(String name);

    @Query("select count(entity.id) from HRDiscipline entity where entity.code =?2 and (entity.id <> ?1 or ?1 is null )")
    Long checkCode(UUID id, String code);

    @Query("select c FROM HRDiscipline c where c.code = ?1")
    List<HRDiscipline> findByCode(String code);

    @Query("delete from HRDiscipline c where c.id in ?1")
    @Modifying
    @Transactional
    void deleteByIds(List<UUID> ids);


    @Query(value = "SELECT code FROM tbl_discipline WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
