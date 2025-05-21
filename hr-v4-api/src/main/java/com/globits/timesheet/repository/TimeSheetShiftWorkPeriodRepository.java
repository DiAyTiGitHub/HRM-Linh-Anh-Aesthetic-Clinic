package com.globits.timesheet.repository;

import com.globits.timesheet.domain.TimeSheetDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.timesheet.domain.TimeSheetShiftWorkPeriod;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSheetShiftWorkPeriodRepository extends JpaRepository<TimeSheetShiftWorkPeriod, UUID> {
    @Query("select distinct entity from TimeSheetShiftWorkPeriod entity " +
            "where entity.timeSheet.staff.id = :staffId " +
            "and entity.shiftWorkTimePeriod.code = :shiftWorkTimePeriodCode " +
            "and entity.timeSheet.startTime >= :fromDate " +
            "and entity.timeSheet.endTime <= :toDate ")
    List<TimeSheetShiftWorkPeriod> findTimeSheetShiftWorkPeriodInRangeTimeOfStaffByShiftWorkTimePeriod(@Param("fromDate") Date fromDate,
                                                                                                       @Param("toDate") Date toDate,
                                                                                                       @Param("staffId") UUID staffId,
                                                                                                       @Param("shiftWorkTimePeriodCode") String shiftWorkTimePeriodCode);
    // find time sheet shift work of staff NOT by shift work time
    @Query("select distinct entity from TimeSheetShiftWorkPeriod entity " +
            "where entity.timeSheet.staff.id = :staffId " +
            "and entity.shiftWorkTimePeriod.code != :shiftWorkTimePeriodCode " +
            "and entity.timeSheet.startTime >= :fromDate " +
            "and entity.timeSheet.endTime <= :toDate ")
    List<TimeSheetShiftWorkPeriod> findTimeSheetShiftWorkPeriodInRangeTimeOfStaffNOTByShiftWorkTimePeriod(@Param("fromDate") Date fromDate,
                                                                                                       @Param("toDate") Date toDate,
                                                                                                       @Param("staffId") UUID staffId,
                                                                                                       @Param("shiftWorkTimePeriodCode") String shiftWorkTimePeriodCode);
}
