package com.globits.hr.repository;

import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.salary.domain.SalaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftWorkTimePeriodRepository extends JpaRepository<ShiftWorkTimePeriod, UUID> {
    @Query("select new com.globits.hr.dto.ShiftWorkTimePeriodDto (entity) from ShiftWorkTimePeriod entity where entity.shiftWork.id = ?1 order by entity.startTime asc")
    List<ShiftWorkTimePeriodDto> getShiftWorkTimePeriodByShiftWorkId(UUID id);

    @Query("select new com.globits.hr.dto.ShiftWorkTimePeriodDto (entity) from ShiftWorkTimePeriod entity order by entity.startTime asc")
    List<ShiftWorkTimePeriodDto> getAllByStartTimeAsc();

    @Query("select entity from ShiftWorkTimePeriod entity WHERE entity.code = ?1")
    ShiftWorkTimePeriod findByCode(String code);

    @Query("SELECT entity FROM ShiftWorkTimePeriod entity " +
            "WHERE entity.shiftWork.id = :shiftWorkId and (" +
            "   TIME(entity.startTime) <= TIME(:currentTime) " +
            "   AND TIME(entity.endTime) >= TIME(:currentTime) " +
            ") OR (" +
            "   TIME(entity.startTime) > TIME(entity.endTime) " +  // Overnight shift case
            "   AND (" +
            "       TIME(:currentTime) >= TIME(entity.startTime) " +
            "       OR TIME(:currentTime) <= TIME(entity.endTime) " +
            "   )" +
            ") ")
    List<ShiftWorkTimePeriod> getCurrentTimePeriod(@Param("currentTime") Date currentTime, @Param("shiftWorkId") UUID shiftWorkId);

    @Query("SELECT entity FROM ShiftWorkTimePeriod entity " +
            "WHERE entity.shiftWork.id = :shiftWorkId " +
            "ORDER BY " +
            "   CASE " +
            "       WHEN TIME(entity.startTime) >= TIME(:currentTime) " +
            "       THEN TIME(entity.startTime) " +
            "       ELSE TIME(entity.endTime) " +
            "   END ASC")
    List<ShiftWorkTimePeriod> getNearestTimePeriod(@Param("currentTime") Date currentTime,
                                                   @Param("shiftWorkId") UUID shiftWorkId);


}
