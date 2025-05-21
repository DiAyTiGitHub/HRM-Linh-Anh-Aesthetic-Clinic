package com.globits.hr.repository;

import com.globits.hr.domain.StaffAnnualLeaveHistory;
import com.globits.hr.domain.StaffMonthlyLeaveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffMonthlyLeaveHistoryRepository extends JpaRepository<StaffMonthlyLeaveHistory, UUID> {
    @Query("select smlh from StaffMonthlyLeaveHistory smlh " +
            "where smlh.annualLeaveHistory.staff.id = :staffId " +
            "and smlh.annualLeaveHistory.year = :year " +
            "and smlh.month = :month")
    List<StaffMonthlyLeaveHistory> findByStaffIdYearAndMonth(@Param("staffId") UUID staffId, @Param("year") Integer year, @Param("month") Integer month);
}
