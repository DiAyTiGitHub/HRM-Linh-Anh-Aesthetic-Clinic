package com.globits.hr.repository;

import com.globits.hr.domain.Allowance;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.HrResourcePlan;
import com.globits.hr.domain.Position;
import com.globits.hr.dto.AllowanceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrResourcePlanRepository extends JpaRepository<HrResourcePlan, UUID> {
    @Query("select entity from HrResourcePlan entity where entity.code = :code")
    List<HrResourcePlan> findByCode(@Param("code") String code);

    @Query("SELECT entity FROM HrResourcePlan entity  WHERE (entity.voided IS NULL OR entity.voided = FALSE) AND entity.department.id = :department ")
    HrResourcePlan findByDepartment(UUID department);

    @Query(value = "SELECT code FROM tbl_department_type WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
