package com.globits.salary.repository;

import com.globits.hr.HrConstants;
import com.globits.salary.domain.SalaryAutoMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryAutoMapRepository extends JpaRepository<SalaryAutoMap, UUID> {
    @Query("select sa FROM SalaryAutoMap sa ")
    List<SalaryAutoMap> findByMapField(@Param("mapField") String mapField);

    @Query("SELECT s FROM SalaryAutoMap s ORDER BY s.salaryAutoMapField ASC")
    List<SalaryAutoMap> findAllSorted();
}
