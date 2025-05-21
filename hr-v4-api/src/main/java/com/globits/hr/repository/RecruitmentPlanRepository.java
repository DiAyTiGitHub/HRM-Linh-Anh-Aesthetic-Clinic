package com.globits.hr.repository;

import com.globits.hr.domain.RecruitmentPlan;
import com.globits.hr.domain.RecruitmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecruitmentPlanRepository extends JpaRepository<RecruitmentPlan, UUID> {
//    @Query("select entity from AllowanceType entity where entity.name =?1")
//    List<AllowanceType> findByName(String name);
//    @Query("select new com.globits.hr.dto.AllowanceTypeDto(s) from AllowanceType s")
//    Page<AllowanceTypeDto> getListPage(Pageable pageable);

    @Query("select rp from RecruitmentPlan rp where rp.code = ?1")
    List<RecruitmentPlan> findByCode(String code);

    @Query(value = "SELECT code FROM tbl_recruitment_plan WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
