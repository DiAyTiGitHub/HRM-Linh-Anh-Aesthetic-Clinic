package com.globits.hr.repository;

import com.globits.hr.domain.StaffWorkScheduleShiftPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffWorkScheduleShiftPeriodRepository extends JpaRepository<StaffWorkScheduleShiftPeriod, UUID> {
    @Query("select entity from StaffWorkScheduleShiftPeriod entity " +
            " where entity.schedule.id = :scheduleId " +
            " and entity.leavePeriod.id = :leavePeriodId")
    List<StaffWorkScheduleShiftPeriod> getByScheduleIdAndLeavePeriodId(
            @Param("scheduleId") UUID scheduleId, @Param("leavePeriodId") UUID leavePeriodId);


    @Query("select entity from StaffWorkScheduleShiftPeriod entity " +
            " where entity.schedule.id = :scheduleId " +
            " and entity.leavePeriod.id = :leavePeriodId")
    Optional<StaffWorkScheduleShiftPeriod> getStaffWorkScheduleShiftPeriodBy(@Param("scheduleId") UUID scheduleId, @Param("leavePeriodId") UUID leavePeriodId);
}
