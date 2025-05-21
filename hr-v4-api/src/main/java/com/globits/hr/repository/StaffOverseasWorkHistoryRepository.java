package com.globits.hr.repository;

import com.globits.hr.domain.StaffOverseasWorkHistory;
import com.globits.hr.dto.StaffOverseasWorkHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface StaffOverseasWorkHistoryRepository extends JpaRepository<StaffOverseasWorkHistory, UUID> {
    @Query("select new com.globits.hr.dto.StaffOverseasWorkHistoryDto(st) from StaffOverseasWorkHistory st")
    Page<StaffOverseasWorkHistoryDto> getPages(Pageable pageable);

    @Query("select new com.globits.hr.dto.StaffOverseasWorkHistoryDto(st) from StaffOverseasWorkHistory st where st.staff.id = ?1")
    List<StaffOverseasWorkHistoryDto> getAll(UUID id);

    @Query("select new com.globits.hr.dto.StaffOverseasWorkHistoryDto(st) from StaffOverseasWorkHistory st where st.id = ?1")
    StaffOverseasWorkHistoryDto getStaffOverseasWorkHistoryById(UUID id);
}
