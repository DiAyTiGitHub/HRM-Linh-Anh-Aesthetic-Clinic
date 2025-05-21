package com.globits.hr.repository;

import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.hr.dto.TotalStaffWorkScheduleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffWorkScheduleRepository extends JpaRepository<StaffWorkSchedule, UUID> {
    @Query("select sws from StaffWorkSchedule sws "
            + " where sws.staff.id IS NOT NULL and sws.shiftWork.id IS NOT NULL and sws.workingDate IS NOT NULL "
            + " and sws.staff.id = ?1 AND Date(sws.workingDate) =?2 "
            + "order by sws.shiftWork.name ")
    List<StaffWorkSchedule> getByStaffAndWorkingDate(UUID staffId, Date workingDate);


    @Query("select sws from StaffWorkSchedule sws "
            + " WHERE (sws.voided = false OR sws.voided is null) "
            + "AND sws.staff.id = :staffId "
            + "AND (:fromDate is null OR Date(sws.workingDate) <= date(:fromDate))  "
            + "AND (:toDate is null OR Date(sws.workingDate) >= date(:toDate))  "
            + " ORDER BY sws.workingDate ASC ")
    List<StaffWorkSchedule> findAllByStaffIdAndWorkingDate(UUID staffId, Date fromDate, Date toDate);

    /*
     *  Lấy những ca được phân có trạng thái:
     *  - Không cần phê duyệt
     *  - Cần phê duyệt với trạng thái đã phê duyệt StaffWorkScheduleApprovalStatus.APPROVED
     */
    @Query("select sws from StaffWorkSchedule sws "
            + " where sws.staff.id IS NOT NULL and sws.shiftWork.id IS NOT NULL and sws.workingDate IS NOT NULL "
            + " and sws.staff.id = ?1 AND DATE(sws.workingDate) = DATE(?2) "
            + "	AND ( sws.needManagerApproval IS NULL OR sws.needManagerApproval = false "
            + " OR ( sws.needManagerApproval = true AND sws.approvalStatus = 2 ) )")
    List<StaffWorkSchedule> getByStaffAndWorkingDateWithNeedManagerApproval(UUID staffId, Date workingDate);

    @Query("SELECT new com.globits.hr.dto.StaffWorkScheduleDto(sws) " +
            "FROM StaffWorkSchedule sws " +
            "where sws.staff.id = ?1 AND Date(sws.workingDate) =?2 " +
            "order by sws.shiftWork.name ")
    List<StaffWorkScheduleDto> getDtoByStaffAndWorkingDate(UUID staffId, Date workingDate);

    @Query("select sws from StaffWorkSchedule sws " +
            "where sws.staff.id = :staffId " +
            "and date(sws.workingDate) = date(:workingDate) " +
            "and sws.shiftWork.id = :shiftWorkId ")
    List<StaffWorkSchedule> getByStaffIdAndShiftWorkIdAndWorkingDate(@Param("staffId") UUID staffId,
                                                                     @Param("shiftWorkId") UUID shiftWorkId,
                                                                     @Param("workingDate") Date workingDate);

    @Query("select sws from StaffWorkSchedule sws " +
            "where trim(sws.staff.staffCode) = :staffCode " +
            "and date(sws.workingDate) = date(:workingDate) " +
            "and trim(sws.shiftWork.code) = :shiftWorkCode ")
    List<StaffWorkSchedule> getByStaffCodeShiftWorkCodeAndWorkingDate(@Param("staffCode") String staffCode,
                                                                     @Param("shiftWorkCode") String shiftWorkCode,
                                                                     @Param("workingDate") Date workingDate);

    @Query("select sws from StaffWorkSchedule sws " +
            "where sws.staff.id = :staffId " +
            "and date(sws.workingDate) = date(:workingDate) " +
            "and sws.shiftWork.code like :shiftWorkCode ")
    List<StaffWorkSchedule> getByStaffIdAndShiftWorkCodeAndWorkingDate(@Param("staffId") UUID staffId,
                                                                       @Param("shiftWorkCode") String shiftWorkCode,
                                                                       @Param("workingDate") Date workingDate);

    @Query("select sws from StaffWorkSchedule sws "
            + "where date(sws.workingDate) >= date(:fromDate) and date(sws.workingDate) <= date(:toDate)")
    List<StaffWorkSchedule> getWorkScheduleInRangeTime(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query("select sws from StaffWorkSchedule sws " + "where sws.staff.id = ?1 AND Date(sws.workingDate) =?2 AND sws.shiftWork.code =?3")
    List<StaffWorkSchedule> getByStaffAndDateAndShiftWorkCode(UUID staffId, Date workingDate, String shiftWorkCode);

    @Query("select sws from StaffWorkSchedule sws " +
            "where sws.staff.id = :staffId " +
            "AND month(sws.workingDate) = :month " +
            "AND year(sws.workingDate) = :year " +
            "order by sws.workingDate")
    List<StaffWorkSchedule> getByStaffIdMonthAndYear(@Param("staffId") UUID staffId,
                                                     @Param("month") Integer month,
                                                     @Param("year") Integer year);

    @Query("SELECT COUNT(sws) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = ?1 " +
            "AND DATE(sws.workingDate) = ?2 " +
            "AND sws.workingStatus = ?3 " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countByStaffAndWorkingDateAndStatus(UUID staffId, Date workingDate, Integer workingStatus);

    @Query("SELECT COALESCE(SUM(sws.lateArrivalCount), 0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = ?1 " +
            "AND DATE(sws.workingDate) = ?2 " +
            "AND (sws.voided IS NULL OR sws.voided = false) " +
            "AND ( (sws.needManagerApproval IS NULL OR sws.needManagerApproval = false) " +
            "      OR (sws.needManagerApproval = true AND sws.approvalStatus = 2) )")
    Long countLateForWork(UUID staffId, Date workingDate);

    @Query("SELECT COALESCE(SUM(sws.totalPaidWork), 0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = ?1 " +
            "AND DATE(sws.workingDate) = ?2 " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double countPaidForWork(UUID staffId, Date workingDate);

    @Query("SELECT COALESCE(SUM(sws.totalHours), 0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = ?1 " +
            "AND DATE(sws.workingDate) = ?2 " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalWorkingHoursByStaffAndDate(UUID staffId, Date workingDate);


    @Query("SELECT COUNT(sws) FROM StaffWorkSchedule sws " + // approvalStatus = Approved
            "WHERE sws.id in (select ar.workSchedule.id from AbsenceRequest ar where ar.approvalStatus = 2 and ar.absenceType = :absenceType and date(ar.workSchedule.workingDate) = date(:workingDate)) " +
            "and sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) = date(:workingDate)")
    Double getTotalShiftsByStaffIdAbsenceTypeAndWorkingDate(@Param("staffId") UUID staffId, @Param("absenceType") Integer absenceType, @Param("workingDate") Date workingDate);


    @Query("SELECT sws FROM StaffWorkSchedule sws " +
            "JOIN sws.shiftWork sw " +
            "JOIN sw.timePeriods t " +
            "WHERE sws.staff.id = :staffId " +  // Get schedule for a specific staff
            "AND date(sws.workingDate) = date(:currentTime) " +  // Filter by today's date
            "AND (" +
            "   FUNCTION('HOUR', t.startTime) * 60 + FUNCTION('MINUTE', t.startTime) " +
            "   <= FUNCTION('HOUR', :currentTime) * 60 + FUNCTION('MINUTE', :currentTime) " +
            "   AND " +
            "   FUNCTION('HOUR', t.endTime) * 60 + FUNCTION('MINUTE', t.endTime) " +
            "   >= FUNCTION('HOUR', :currentTime) * 60 + FUNCTION('MINUTE', :currentTime) " +
            ") OR (" +
            "   FUNCTION('HOUR', t.startTime) * 60 + FUNCTION('MINUTE', t.startTime) " +
            "   > FUNCTION('HOUR', t.endTime) * 60 + FUNCTION('MINUTE', t.endTime) " +
            "   AND (" +
            "       FUNCTION('HOUR', :currentTime) * 60 + FUNCTION('MINUTE', :currentTime) " +
            "       >= FUNCTION('HOUR', t.startTime) * 60 + FUNCTION('MINUTE', t.startTime) " +
            "       OR " +
            "       FUNCTION('HOUR', :currentTime) * 60 + FUNCTION('MINUTE', :currentTime) " +
            "       <= FUNCTION('HOUR', t.endTime) * 60 + FUNCTION('MINUTE', t.endTime) " +
            "   )" +
            ")")
    List<StaffWorkSchedule> findCurrentScheduleByStaffIdAndCurrentTime(
            @Param("staffId") UUID staffId,
            @Param("currentTime") Date currentTime
    );

    @Query("SELECT sws FROM StaffWorkSchedule sws " +
            "JOIN sws.shiftWork sw " +
            "JOIN sw.timePeriods t " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) = date(:currentTime) " +
            "ORDER BY " +
            "   CASE " +
            "       WHEN FUNCTION('HOUR', t.startTime) * 60 + FUNCTION('MINUTE', t.startTime) " +
            "            >= FUNCTION('HOUR', :currentTime) * 60 + FUNCTION('MINUTE', :currentTime) " +
            "       THEN FUNCTION('HOUR', t.startTime) * 60 + FUNCTION('MINUTE', t.startTime) " +
            "       ELSE FUNCTION('HOUR', t.endTime) * 60 + FUNCTION('MINUTE', t.endTime) " +
            "   END ASC")
    List<StaffWorkSchedule> findNearestScheduleByStaffIdAndCurrentTime(
            @Param("staffId") UUID staffId,
            @Param("currentTime") Date currentTime
    );

    @Query("SELECT sws FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) = DATE(:workingDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    List<StaffWorkSchedule> findByStaffIdAndWorkingDate(
            @Param("staffId") UUID staffId,
            @Param("workingDate") Date workingDate
    );

    @Query("SELECT sws FROM StaffWorkSchedule sws WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    List<StaffWorkSchedule> findByStaffIdAndWorkingDateBetween(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    ///  DÙNG TRONG THỐNG KÊ, TÍNH LƯƠNG
    @Query("SELECT COUNT(sws) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "AND sws.workingStatus = :workingStatus " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countByStaffIdWorkingStatusAndWorkingDateBetween(
            @Param("staffId") UUID staffId,
            @Param("workingStatus") Integer workingStatus,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("SELECT COUNT(sws) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "AND sws.leaveType.code = :leaveTypeCode " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countByStaffIdLeaveTypeAndWorkingDateBetween(
            @Param("staffId") UUID staffId,
            @Param("leaveTypeCode") String leaveTypeCode,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Số lần đi muon
    @Query("SELECT COALESCE(SUM(COALESCE(sws.lateArrivalCount, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countTotalLateArrivals(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Số lần về sớm
    @Query("SELECT COALESCE(SUM(COALESCE(sws.earlyExitCount, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countTotalEarlyExits(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Tính số phút đi muộn
    @Query("SELECT COALESCE(SUM(COALESCE(sws.lateArrivalMinutes, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countTotalLateArrivalMinutes(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Tính số phút về sớm
    @Query("SELECT COALESCE(SUM(COALESCE(sws.earlyExitMinutes, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countTotalEarlyExitMinutes(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Tính số phút đi sớm
    @Query("SELECT COALESCE(SUM(COALESCE(sws.earlyArrivalMinutes, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countTotalEarlyArrivalMinutes(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Tính số phút về muộn
    @Query("SELECT COALESCE(SUM(COALESCE(sws.lateExitMinutes, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Long countTotalLateExitMinutes(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Tính số công được tính
    @Query("SELECT COALESCE(SUM(COALESCE(sws.totalPaidWork, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumTotalPaidWork(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Tổng số giờ làm việc được phân
    @Query("SELECT COALESCE(SUM(COALESCE(sws.shiftWork.totalHours, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumTotalAssignedHours(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Tổng số giờ làm việc thực tế (đã chấm công)
    @Query("SELECT COALESCE(SUM(COALESCE(sws.totalHours, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumActualWorkedHours(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Tổng số giờ làm thêm trước ca (OT trước ca)
    @Query("SELECT COALESCE(SUM(COALESCE(sws.confirmedOTHoursBeforeShift, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumConfirmedOTHoursBeforeShift(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Tổng số giờ làm thêm sau ca (OT sau ca)
    @Query("SELECT COALESCE(SUM(COALESCE(sws.confirmedOTHoursAfterShift, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumConfirmedOTHoursAfterShift(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Tổng số giờ làm việc công quy đổi
    @Query("SELECT COALESCE(SUM(COALESCE(sws.convertedWorkingHours, 0)),0) FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND DATE(sws.workingDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumConvertedWorkingHours(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Tổng OT thử việc: trước giờ và sau giờ, trước ngày chính thức
    @Query("SELECT COALESCE(SUM(COALESCE(sws.confirmedOTHoursBeforeShift, 0) + COALESCE(sws.confirmedOTHoursAfterShift, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) < date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) ")
    Double sumOTDuringProbation(
            @Param("staffId") UUID staffId,
            @Param("officialDate") Date officialDate,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Tổng số giờ OT được xác nhận trong khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(sws.confirmedOTHoursBeforeShift, 0) + COALESCE(sws.confirmedOTHoursAfterShift, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) ")
    Double sumConfirmedOTHoursInRangeTime(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Tổng OT chính thức: trước giờ và sau giờ, trước ngày chính thức
    @Query("SELECT COALESCE(SUM(COALESCE(sws.confirmedOTHoursBeforeShift, 0) + COALESCE(sws.confirmedOTHoursAfterShift, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) >= date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) ")
    Double sumOTDuringOfficialStaff(
            @Param("staffId") UUID staffId,
            @Param("officialDate") Date officialDate,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Tổng OT chính thức: trước giờ và sau giờ, trước ngày chính thức
    @Query("SELECT SUM(COALESCE(sws.totalValidHours, 0)) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumValidWorkingHoursStaff(
            @Param("staffId") UUID staffId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Truy vấn tổng số phút trễ sớm trong thử việc
    @Query("SELECT COALESCE(SUM(COALESCE(sws.lateArrivalMinutes, 0) + COALESCE(sws.earlyExitMinutes, 0)),0), sws.workingDate " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) < date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) ) " +
            "GROUP BY sws.workingDate")
    List<Object[]> getObjectLateArrivalAndEarlyExitDuringProbation(
            @Param("staffId") UUID staffId,
            @Param("officialDate") Date officialDate,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Truy vấn tổng số phút trễ sớm trong thử việc
    @Query("SELECT COALESCE(SUM(COALESCE(sws.lateArrivalMinutes, 0) + COALESCE(sws.earlyExitMinutes, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) < date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumLateArrivalAndEarlyExitDuringProbation(
            @Param("staffId") UUID staffId,
            @Param("officialDate") Date officialDate,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Truy vấn tổng số phút trễ sớm trong giai đoạn chính thức
    @Query("SELECT " +
            "COALESCE(SUM(COALESCE(sws.lateArrivalMinutes, 0) + COALESCE(sws.earlyExitMinutes, 0)), 0), " +
            "sws.workingDate " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND sws.workingDate >= :officialDate " +
            "AND sws.workingDate BETWEEN :fromDate AND :toDate " +
            "AND (sws.voided IS NULL OR sws.voided = false) " +
            "AND ( (sws.needManagerApproval IS NULL OR sws.needManagerApproval = false) " +
            "   OR (sws.needManagerApproval = true AND sws.approvalStatus = 2) ) " +
            "GROUP BY sws.workingDate")
    List<Object[]> getObjectLateArrivalAndEarlyExitDuringOfficialStaff(
            @Param("staffId") UUID staffId,
            @Param("officialDate") Date officialDate,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Truy vấn tổng số phút trễ sớm trong giai đoạn chính thức
    @Query("SELECT COALESCE(SUM(COALESCE(sws.lateArrivalMinutes, 0) + COALESCE(sws.earlyExitMinutes, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) >= date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double sumLateArrivalAndEarlyExitDuringOfficialStaff(
            @Param("staffId") UUID staffId,
            @Param("officialDate") Date officialDate,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );


    // Tổng ngày công được phân trong 1 kỳ lương của 1 nhân viên
    @Query("SELECT COALESCE(SUM(COALESCE(stwtp.workRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "JOIN sws.shiftWork sw " +
            "JOIN sw.timePeriods stwtp " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalWorkRatio(@Param("staffId") UUID staffId,
                             @Param("fromDate") Date fromDate,
                             @Param("toDate") Date toDate);

    // Tổng ngày công thử việc được phân trong 1 kỳ lương của 1 nhân viên
    @Query("SELECT COALESCE(SUM(COALESCE(stwtp.workRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "JOIN sws.shiftWork sw " +
            "JOIN sw.timePeriods stwtp " +
            "WHERE sws.staff.id = :staffId " +
            "and date(sws.workingDate) < date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalProbationWorkRatio(@Param("staffId") UUID staffId,
                                      @Param("officialDate") Date officialDate,
                                      @Param("fromDate") Date fromDate,
                                      @Param("toDate") Date toDate);


    // Tổng ngày công thử việc nhân viên thực tế đã làm trong 1 khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(sws.totalPaidWork, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "and date(sws.workingDate) < date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalProbationPaidWork(@Param("staffId") UUID staffId,
                                     @Param("officialDate") Date officialDate,
                                     @Param("fromDate") Date fromDate,
                                     @Param("toDate") Date toDate);

    // Tổng ngày công thử việc nghỉ phép được hưởng lương
    @Query("SELECT COALESCE(SUM(COALESCE(sws.paidLeaveWorkRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "and date(sws.workingDate) < date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalProbationPaidLeaveWork(@Param("staffId") UUID staffId,
                                          @Param("officialDate") Date officialDate,
                                          @Param("fromDate") Date fromDate,
                                          @Param("toDate") Date toDate);


    // Tổng ngày công chính thức được phân trong 1 kỳ lương của 1 nhân viên
    @Query("SELECT COALESCE(SUM(COALESCE(stwtp.workRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "JOIN sws.shiftWork sw " +
            "JOIN sw.timePeriods stwtp " +
            "WHERE sws.staff.id = :staffId " +
            "and date(sws.workingDate) >= date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalOfficialStaffWorkRatio(@Param("staffId") UUID staffId,
                                          @Param("officialDate") Date officialDate,
                                          @Param("fromDate") Date fromDate,
                                          @Param("toDate") Date toDate);

    // Tổng ngày công chính thức nhân viên thực tế đã làm trong 1 khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(sws.totalPaidWork, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "and date(sws.workingDate) >= date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalOfficialStaffPaidWork(@Param("staffId") UUID staffId,
                                         @Param("officialDate") Date officialDate,
                                         @Param("fromDate") Date fromDate,
                                         @Param("toDate") Date toDate);

    // Tổng ngày công chính thức nhân viên thực tế đã làm trong 1 khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(sws.paidLeaveWorkRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "and date(sws.workingDate) >= date(:officialDate) " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalOfficialStaffPaidLeaveWork(@Param("staffId") UUID staffId,
                                              @Param("officialDate") Date officialDate,
                                              @Param("fromDate") Date fromDate,
                                              @Param("toDate") Date toDate);


    // Tổng ngày công nhân viên thực tế đã làm trong 1 khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(sws.totalPaidWork, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalPaidWork(@Param("staffId") UUID staffId,
                            @Param("fromDate") Date fromDate,
                            @Param("toDate") Date toDate);

    // Tổng ngày công nhân viên thực tế đã làm + nghỉ có phép trong 1 khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(sws.totalPaidWork, 0) + COALESCE(sws.paidLeaveWorkRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalPaidWorkAndLeaveWorkRatio(@Param("staffId") UUID staffId,
                                             @Param("fromDate") Date fromDate,
                                             @Param("toDate") Date toDate);


    // Tổng ngày công nhân viên thực tế nghỉ phép trong 1 khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(stwtp.workRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "JOIN sws.shiftWork sw " +
            "JOIN sw.timePeriods stwtp " +
            "WHERE sws.staff.id = :staffId " +
            "AND sws.leaveType.code = :leaveTypeCode " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalPaidWorkByLeaveTypeOfStaff(@Param("staffId") UUID staffId,
                                              @Param("leaveTypeCode") String leaveTypeCode,
                                              @Param("fromDate") Date fromDate,
                                              @Param("toDate") Date toDate);

    // Tổng ngày công thử việc nhân viên  nghỉ phép trong 1 khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(stwtp.workRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "JOIN sws.shiftWork sw " +
            "JOIN sw.timePeriods stwtp " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) < date(:officialDate) " +
            "AND sws.leaveType.code = :leaveTypeCode " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalProbationPaidWorkByLeaveTypeOfStaff(@Param("staffId") UUID staffId,
                                                       @Param("officialDate") Date officialDate,
                                                       @Param("leaveTypeCode") String leaveTypeCode,
                                                       @Param("fromDate") Date fromDate,
                                                       @Param("toDate") Date toDate);


    // Tổng ngày công chính thức nhân viên nghỉ phép trong 1 khoảng thời gian
    @Query("SELECT COALESCE(SUM(COALESCE(stwtp.workRatio, 0)),0) " +
            "FROM StaffWorkSchedule sws " +
            "JOIN sws.shiftWork sw " +
            "JOIN sw.timePeriods stwtp " +
            "WHERE sws.staff.id = :staffId " +
            "AND date(sws.workingDate) >= date(:officialDate) " +
            "AND sws.leaveType.code = :leaveTypeCode " +
            "AND date(sws.workingDate) BETWEEN date(:fromDate) AND date(:toDate) " +
            "and (sws.voided is null or sws.voided = false) " +
            "and ( (sws.needManagerApproval is null or sws.needManagerApproval = false) or (sws.needManagerApproval = true and sws.approvalStatus = 2) )")
    Double getTotalOfficialPaidWorkByLeaveTypeOfStaff(@Param("staffId") UUID staffId,
                                                      @Param("officialDate") Date officialDate,
                                                      @Param("leaveTypeCode") String leaveTypeCode,
                                                      @Param("fromDate") Date fromDate,
                                                      @Param("toDate") Date toDate);


    @Query("SELECT new com.globits.hr.dto.TotalStaffWorkScheduleDto(" +
            "entity.staff.staffCode, " +
            "entity.staff.displayName, " +
            "SUM(entity.estimatedWorkingHours), " +
            "SUM(entity.lateArrivalCount), " +
            "SUM(entity.earlyExitCount), " +
            "SUM(entity.lateArrivalMinutes), " +
            "SUM(entity.earlyExitMinutes), " +
            "SUM(entity.earlyArrivalMinutes), " +
            "SUM(entity.lateExitMinutes), " +
            "SUM(entity.confirmedOTHoursBeforeShift), " +
            "SUM(entity.confirmedOTHoursAfterShift), " +
            "SUM(entity.totalHours), " +
            "SUM(entity.totalValidHours), " +
            "SUM(entity.paidLeaveHours), " +
            "SUM(entity.unpaidLeaveHours), " +
            "SUM(entity.convertedWorkingHours), " +
            "SUM(entity.totalPaidWork), " +
            "SUM(entity.unpaidLeaveWorkRatio), " +
            "SUM(entity.paidLeaveWorkRatio)) " +
            "FROM StaffWorkSchedule entity " +
            "WHERE entity.staff.id = :staffId " +
            "AND entity.workingDate >= :fromDate " +
            "AND entity.workingDate <= :toDate " +
            "GROUP BY entity.staff.id")
    TotalStaffWorkScheduleDto getStaffWorkScheduleSummary(@Param("staffId") UUID staffId,
                                                          @Param("fromDate") Date fromDate,
                                                          @Param("toDate") Date toDate);

    // Khi không có staffId
    @Query("SELECT new com.globits.hr.dto.TotalStaffWorkScheduleDto(" +
            "null, null, " + // vì không group theo nhân viên nữa
            "SUM(entity.estimatedWorkingHours), " +
            "SUM(entity.lateArrivalCount), " +
            "SUM(entity.earlyExitCount), " +
            "SUM(entity.lateArrivalMinutes), " +
            "SUM(entity.earlyExitMinutes), " +
            "SUM(entity.earlyArrivalMinutes), " +
            "SUM(entity.lateExitMinutes), " +
            "SUM(entity.confirmedOTHoursBeforeShift), " +
            "SUM(entity.confirmedOTHoursAfterShift), " +
            "SUM(entity.totalHours), " +
            "SUM(entity.totalValidHours), " +
            "SUM(entity.paidLeaveHours), " +
            "SUM(entity.unpaidLeaveHours), " +
            "SUM(entity.convertedWorkingHours), " +
            "SUM(entity.totalPaidWork), " +
            "SUM(entity.unpaidLeaveWorkRatio), " +
            "SUM(entity.paidLeaveWorkRatio)) " +
            "FROM StaffWorkSchedule entity " +
            "WHERE entity.workingDate >= :fromDate " +
            "AND entity.workingDate <= :toDate")
    TotalStaffWorkScheduleDto getSummaryAllStaff(@Param("fromDate") Date fromDate,
                                                 @Param("toDate") Date toDate);

    @Query("SELECT sws FROM StaffWorkSchedule sws " +
            " WHERE sws.staff.id = :staffId " +
            " AND DATE(sws.workingDate) BETWEEN DATE(:startDate) AND DATE(:endDate)" +
            " AND (sws.voided is null or sws.voided = false) " +
            " AND sws.leaveType.code = :leaveCode ")
    List<StaffWorkSchedule> getApprovedAnnualLeaveDays(UUID staffId, String leaveCode, Date startDate, Date endDate);

    // Tổng tỷ lệ ngày nghỉ trong 1 năm
    @Query("""
                SELECT COALESCE(SUM(s.paidLeaveWorkRatio), 0) + COALESCE(SUM(s.unpaidLeaveWorkRatio), 0)
                FROM StaffWorkSchedule s
                WHERE s.staff.id = :staffId
                  AND FUNCTION('YEAR', s.workingDate) = :year
            """)
    Double getTotalLeaveRatioByStaffAndYear(@Param("staffId") UUID staffId, @Param("year") int year);


    // Tổng giờ OT trong năm
    @Query("""
                SELECT COALESCE(SUM(s.confirmedOTHoursBeforeShift), 0) + COALESCE(SUM(s.confirmedOTHoursAfterShift), 0)
                FROM StaffWorkSchedule s
                WHERE s.staff.id = :staffId
                  AND FUNCTION('YEAR', s.workingDate) = :year
            """)
    Double getTotalConfirmedOTHoursByStaffAndYear(@Param("staffId") UUID staffId, @Param("year") int year);


}
