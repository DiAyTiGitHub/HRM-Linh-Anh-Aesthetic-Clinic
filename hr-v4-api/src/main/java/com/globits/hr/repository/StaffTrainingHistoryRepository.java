package com.globits.hr.repository;

import com.globits.hr.domain.StaffTrainingHistory;
import com.globits.hr.dto.StaffTrainingHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffTrainingHistoryRepository extends JpaRepository<StaffTrainingHistory, UUID> {
    @Query("select new com.globits.hr.dto.StaffTrainingHistoryDto(st) from StaffTrainingHistory st")
    Page<StaffTrainingHistoryDto> getPages(Pageable pageable);

    @Query("select new com.globits.hr.dto.StaffTrainingHistoryDto(st) from StaffTrainingHistory st where st.staff.id = ?1")
    List<StaffTrainingHistoryDto> getAll(UUID id);

    @Query("select new com.globits.hr.dto.StaffTrainingHistoryDto(st) from StaffTrainingHistory st where st.id = ?1")
    StaffTrainingHistoryDto getStaffTrainingHistoryById(UUID id);
}
