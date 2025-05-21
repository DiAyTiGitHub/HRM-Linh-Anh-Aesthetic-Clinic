package com.globits.hr.repository;

import com.globits.hr.domain.CandidateEducationHistory;
import com.globits.hr.domain.CandidateWorkingExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CandidateWorkingExperienceRepository extends JpaRepository<CandidateWorkingExperience, UUID> {

//    @Query("select new com.globits.hr.dto.StaffEducationHistoryDto(education) from StaffEducationHistory education where education.id = ?1")
//    StaffEducationHistoryDto getEducationById(UUID id);
//
//    @Query("select new com.globits.hr.dto.StaffEducationHistoryDto(education) from StaffEducationHistory education where education.staff.id = ?1")
//    List<StaffEducationHistoryDto> getAll(UUID id);
//
//    @Query("select new com.globits.hr.dto.StaffEducationHistoryDto(education) from StaffEducationHistory education")
//    Page<StaffEducationHistoryDto> getPages(Pageable pageable);

}
