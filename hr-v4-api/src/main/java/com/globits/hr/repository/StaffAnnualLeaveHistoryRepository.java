package com.globits.hr.repository;

import com.globits.hr.domain.StaffAnnualLeaveHistory;
import com.globits.salary.domain.SalaryTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffAnnualLeaveHistoryRepository extends JpaRepository<StaffAnnualLeaveHistory, UUID> {
    @Query("select salh from StaffAnnualLeaveHistory salh where salh.staff.id = :staffId and salh.year = :year")
    List<StaffAnnualLeaveHistory> findByStaffIdAndYear(@Param("staffId") UUID staffId, @Param("year") Integer year);
}
