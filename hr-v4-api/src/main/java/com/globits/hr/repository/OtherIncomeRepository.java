package com.globits.hr.repository;

import com.globits.hr.domain.OtherIncome;
import com.globits.salary.domain.SalaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OtherIncomeRepository extends JpaRepository<OtherIncome, UUID> {
    @Query("select oi from OtherIncome oi " +
            "where oi.type = :incomeType and oi.staff.id = :staffId " +
            "and oi.salaryPeriod.id = :salaryPeriodId ")
    List<OtherIncome> findByIncomeTypeStaffIdAndPeriodId(@Param("incomeType") Integer incomeType,
                                                         @Param("staffId") UUID staffId,
                                                         @Param("salaryPeriodId") UUID salaryPeriodId);
}
