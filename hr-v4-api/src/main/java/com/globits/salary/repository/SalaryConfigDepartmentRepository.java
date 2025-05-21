package com.globits.salary.repository;

import com.globits.salary.domain.SalaryConfig;
import com.globits.salary.domain.SalaryConfigDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryConfigDepartmentRepository extends JpaRepository<SalaryConfigDepartment, UUID> {
    @Query("Select entity from SalaryConfigDepartment entity " +
            "where entity.department.id=:idDepartment and entity.salaryConfig.id=:idSalaryConfig")
    List<SalaryConfigDepartment> findByDepartmentIdAndSalaryConfigId(@Param("idDepartment") UUID idDepartment,
                                                                   @Param("idSalaryConfig") UUID idSalaryConfig);
}
