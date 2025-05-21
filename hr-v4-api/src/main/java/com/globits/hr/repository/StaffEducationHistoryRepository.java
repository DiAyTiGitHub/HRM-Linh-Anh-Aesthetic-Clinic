package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.StaffEducationHistory;
import com.globits.hr.dto.StaffEducationHistoryDto;

@Repository
public interface StaffEducationHistoryRepository extends JpaRepository<StaffEducationHistory, UUID> {

    @Query("select new com.globits.hr.dto.StaffEducationHistoryDto(education) from StaffEducationHistory education where education.id = ?1")
    StaffEducationHistoryDto getEducationById(UUID id);

    @Query("select new com.globits.hr.dto.StaffEducationHistoryDto(education) from StaffEducationHistory education where education.staff.id = ?1")
    List<StaffEducationHistoryDto> getAll(UUID id);

    @Query("select new com.globits.hr.dto.StaffEducationHistoryDto(education) from StaffEducationHistory education")
    Page<StaffEducationHistoryDto> getPages(Pageable pageable);

}
