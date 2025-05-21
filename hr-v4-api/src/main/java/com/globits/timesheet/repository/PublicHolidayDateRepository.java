package com.globits.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import com.globits.timesheet.domain.PublicHolidayDate;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublicHolidayDateRepository extends JpaRepository<PublicHolidayDate, UUID> {
    @Query("select entity from PublicHolidayDate entity where entity.holidayType = ?1")
    List<PublicHolidayDate> findByType(Integer type);

    @Query("select entity from PublicHolidayDate entity " +
            "where date(entity.holidayDate) BETWEEN date(:fromDate) AND date(:toDate) ")
    List<PublicHolidayDate> getInRangeTime(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query("select entity from PublicHolidayDate entity where DATE(entity.holidayDate) = DATE(?1)")
    Optional<PublicHolidayDate> findByHolidayDate(Date holidayDate);

    @Query("SELECT COUNT(entity) FROM PublicHolidayDate entity WHERE entity.holidayDate BETWEEN ?1 AND ?2")
    Long countHolidaysBetween(Date fromDate, Date toDate);

    @Query("""
                SELECT SUM(CASE 
                             WHEN entity.isHalfDayOff = true THEN 0.5 
                             ELSE 1.0 
                           END) 
                FROM PublicHolidayDate entity 
                WHERE entity.holidayDate BETWEEN ?1 AND ?2
            """)
    Double getLeaveDayRatioHolidaysBetween(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query("""
                SELECT COALESCE(SUM(entity.leaveHours), 0) 
                FROM PublicHolidayDate entity 
                WHERE date(entity.holidayDate) BETWEEN date(:fromDate) AND date(:toDate)
            """)
    Double getTotalLeaveHoursInRangeTime(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);


    @Query("SELECT COALESCE(SUM(sws.totalPaidWork), 0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = ?1 " +
            "AND DATE(sws.workingDate) = ?2 " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double countPaidForWork(UUID staffId, Date workingDate);


}
