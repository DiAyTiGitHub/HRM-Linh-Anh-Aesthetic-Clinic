package com.globits.hr.repository;


import com.globits.hr.domain.StaffSocialInsurance;
import com.globits.hr.dto.StaffMaternityHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffSocialInsuranceRepository extends JpaRepository<StaffSocialInsurance, UUID> {
    @Query("select sci from StaffSocialInsurance sci " +
            "where sci.staff.id = :staffId and sci.salaryResult.id = :salaryResultId ")
    List<StaffSocialInsurance> findByStaffIdAndSalaryResultId(@Param("staffId") UUID staffId, @Param("salaryResultId") UUID salaryResultId);

    @Query("select sci from StaffSocialInsurance sci " +
            "where sci.staff.id = :staffId and sci.salaryPeriod.id = :salaryPeriodId ")
    List<StaffSocialInsurance> findByStaffIdAndSalaryPeriodId(@Param("staffId") UUID staffId, @Param("salaryPeriodId") UUID salaryPeriodId);
}
