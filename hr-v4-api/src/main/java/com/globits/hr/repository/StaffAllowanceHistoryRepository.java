package com.globits.hr.repository;

import com.globits.hr.domain.StaffAllowanceHistory;
import com.globits.hr.dto.StaffAllowanceHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffAllowanceHistoryRepository extends JpaRepository<StaffAllowanceHistory, UUID> {
    @Query("select new com.globits.hr.dto.StaffAllowanceHistoryDto(st) from StaffAllowanceHistory st")
    Page<StaffAllowanceHistoryDto> getPages(Pageable pageable);

    @Query("select new com.globits.hr.dto.StaffAllowanceHistoryDto(st) from StaffAllowanceHistory st where st.staff.id = ?1")
    List<StaffAllowanceHistoryDto> getAll(UUID id);

    @Query("select new com.globits.hr.dto.StaffAllowanceHistoryDto(st) from StaffAllowanceHistory st where st.id = ?1")
    StaffAllowanceHistoryDto getStaffAllowanceHistoryById(UUID id);
}
