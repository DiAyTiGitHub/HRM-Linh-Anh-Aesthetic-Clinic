package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.StaffLabourAgreementDto;
import org.springframework.data.jpa.repository.JpaRepository;

import com.globits.hr.domain.StaffInsuranceHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StaffInsuranceHistoryRepository extends JpaRepository<StaffInsuranceHistory, UUID> {
    @Query("SELECT entity from StaffInsuranceHistory entity " +
            "WHERE entity.staff.id = :staffId " +
            "order by entity.startDate desc, entity.endDate desc ")
    List<StaffInsuranceHistory> findByStaffId(@Param("staffId") UUID staffId);
}
