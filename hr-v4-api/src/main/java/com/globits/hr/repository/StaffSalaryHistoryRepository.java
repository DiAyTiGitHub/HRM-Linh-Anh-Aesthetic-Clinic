package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.StaffEducationHistoryDto;
import com.globits.hr.dto.StaffSalaryHistoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.StaffSalaryHistory;

@Repository
public interface StaffSalaryHistoryRepository extends JpaRepository<StaffSalaryHistory, UUID> {
    @Query("select new com.globits.hr.dto.StaffSalaryHistoryDto(salary) from StaffSalaryHistory salary where salary.staff.id = ?1")
    List<StaffSalaryHistoryDto> getAllByStaffId(UUID id);
}
