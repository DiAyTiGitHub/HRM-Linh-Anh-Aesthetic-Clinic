package com.globits.salary.repository;

import com.globits.salary.domain.SalaryPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryPeriodRepository extends JpaRepository<SalaryPeriod, UUID> {

    @Query("select e FROM SalaryPeriod e where e.code = ?1")
    List<SalaryPeriod> findByCode(String code);
    
    @Query("Select entity From SalaryPeriod entity " +
            "where (entity.voided is null or entity.voided = false) " +
            "and (DATE(entity.fromDate) <= DATE(:requestDate) " +
            "and DATE(entity.toDate) >= DATE(:requestDate))")
	List<SalaryPeriod> getActivePeriodsByDate(@Param("requestDate") Date requestDate);

    @Query("SELECT entity FROM SalaryPeriod entity " +
            "WHERE (entity.voided IS NULL OR entity.voided = false) " +
            "AND entity.fromDate <= :toDate " +
            "AND entity.toDate >= :fromDate")
    List<SalaryPeriod> findSalaryPeriodsOverlapWithRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(value = "SELECT code " +
            "FROM tbl_salary_period " +
            "WHERE code LIKE CONCAT(:prefix, '%/%') " +
            "ORDER BY CAST(REPLACE(SUBSTRING_INDEX(code, '/', 1), :prefix, '') AS UNSIGNED) DESC " +
            "LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix);

}

