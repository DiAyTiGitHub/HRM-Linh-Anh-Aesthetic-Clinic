package com.globits.hr.repository;

import com.globits.hr.domain.StaffMaternityHistory;
import com.globits.hr.dto.StaffMaternityHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffMaternityHistoryRepository extends JpaRepository<StaffMaternityHistory, UUID> {
    @Query("select new com.globits.hr.dto.StaffMaternityHistoryDto(st) from StaffMaternityHistory st")
    Page<StaffMaternityHistoryDto> getPages(Pageable pageable);

    @Query("select new com.globits.hr.dto.StaffMaternityHistoryDto(st) from StaffMaternityHistory st where st.staff.id = ?1")
    List<StaffMaternityHistoryDto> getAll(UUID id);

    @Query("select new com.globits.hr.dto.StaffMaternityHistoryDto(st) from StaffMaternityHistory st where st.id = ?1")
    StaffMaternityHistoryDto getStaffMaternityHistoryById(UUID id);

    @Query("SELECT smh FROM StaffMaternityHistory smh " +
            "WHERE smh.staff.id = :staffId " +
            "AND (" +
            "(smh.startDate IS NOT NULL AND smh.startDate <= :toDate) " +
            "OR " +
            "(smh.endDate IS NOT NULL AND smh.endDate >= :fromDate)" +
            ")")
    List<StaffMaternityHistory> findConjunctionInRangeTimeOfStaff(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("SELECT smh FROM StaffMaternityHistory smh " +
            "WHERE smh.staff.id = :staffId " +
            "AND date(smh.startDate) <= date(:includedDate) and date(smh.endDate) >= date(:includedDate) "
    )
    List<StaffMaternityHistory> findByIncludedDate(
            @Param("staffId") UUID staffId,
            @Param("includedDate") Date includedDate
    );

    @Query("""
                SELECT s FROM StaffMaternityHistory s
                WHERE s.staff.id = :staffId
                  AND (
                       FUNCTION('YEAR', s.startDate) = :currentYear
                    OR FUNCTION('YEAR', s.endDate) = :currentYear
                  )
                  order by s.startDate, s.endDate
            """)
    List<StaffMaternityHistory> findMaternityHistoryInCurrentYear(@Param("staffId") UUID staffId, @Param("currentYear") int currentYear);


}
