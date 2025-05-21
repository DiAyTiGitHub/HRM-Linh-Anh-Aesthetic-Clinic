package com.globits.salary.repository;

import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.SalaryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryResultRepository extends JpaRepository<SalaryResult, UUID> {

    @Query("select sr FROM SalaryResult sr where sr.code = ?1")
    List<SalaryResult> findByCode(String code);

    @Query("select sr FROM SalaryResult sr " +
            "where sr.salaryTemplate.id = :salaryTemplateId " +
            "and sr.salaryPeriod.id = :salaryPeriodId ")
    List<SalaryResult> findByTemplateIdAndPeriodId(@Param("salaryTemplateId") UUID salaryTemplateId,
                                                   @Param("salaryPeriodId") UUID salaryPeriodId);

}

