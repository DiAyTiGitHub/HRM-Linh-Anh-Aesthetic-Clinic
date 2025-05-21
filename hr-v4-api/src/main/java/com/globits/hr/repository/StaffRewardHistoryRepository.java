package com.globits.hr.repository;

import com.globits.hr.domain.StaffRewardHistory;
import com.globits.hr.dto.StaffRewardHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffRewardHistoryRepository  extends JpaRepository<StaffRewardHistory, UUID> {
    @Query("select new com.globits.hr.dto.StaffRewardHistoryDto(st) from StaffRewardHistory st")
    Page<StaffRewardHistoryDto> getPages(Pageable pageable);

    @Query("select new com.globits.hr.dto.StaffRewardHistoryDto(st) from StaffRewardHistory st where st.staff.id = ?1")
    List<StaffRewardHistoryDto> getAll(UUID id);

    @Query("select new com.globits.hr.dto.StaffRewardHistoryDto(st) from StaffRewardHistory st where st.id = ?1")
    StaffRewardHistoryDto getStaffRewardHistoryById(UUID id);
}
