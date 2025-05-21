package com.globits.hr.repository;

import com.globits.hr.domain.HrOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrOrganizationRepository extends JpaRepository<HrOrganization, UUID> {
    @Query("select count(entity.id) from HrOrganization entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select c FROM HrOrganization c where c.code = ?1")
    List<HrOrganization> findByCode(String code);

    @Query(value = "SELECT code FROM tbl_organization WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
