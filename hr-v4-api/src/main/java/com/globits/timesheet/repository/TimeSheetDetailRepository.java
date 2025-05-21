package com.globits.timesheet.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.timesheet.dto.calendar.ScheduledTimesheetDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.TimeSheetDetailDto;

@Repository
public interface TimeSheetDetailRepository extends JpaRepository<TimeSheetDetail, UUID> {
    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(sc) from TimeSheetDetail sc")
    Page<TimeSheetDetailDto> getListPage(Pageable pageable);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(ts) from TimeSheetDetail ts where ts.id=?1")
    TimeSheetDetailDto findTimeSheetDetailById(UUID id);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(ts) from TimeSheetDetail ts where ts.timeSheet.id=?1")
    List<TimeSheetDetailDto> findTimeSheetDetailByTimeSheetId(UUID id);

    @Query("select ts from TimeSheetDetail ts where ts.timeSheet.id= ?1")
    List<TimeSheetDetail> findTimeSheetDetailEntitiesByTimeSheetId(UUID timesheetId);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t where t.activity.id = ?1")
    List<TimeSheetDetailDto> getListTimeSheetDetailByProjectActivityId(UUID id);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t where t.project.id = ?1")
    List<TimeSheetDetailDto> getListTimeSheetDetailByProjectId(UUID id);

    @Query(value = "select a.id from tbl_timesheet_detail a ", nativeQuery = true)
    List<UUID> getTimeSheetDetailId();

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t where t.task.id = ?1 ")
    List<TimeSheetDetailDto> getListTimeSheetDetailByTaskId(UUID id);

    @Query("select t from TimeSheetDetail t where t.task.id = ?1 ")
    List<TimeSheetDetail> getListTimeSheetDetailByTaskIdNew(UUID id);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t where t.task.id = ?1 and t.employee.id = ?1")
    List<TimeSheetDetailDto> getListTimeSheetDetailByTaskIdAndStaffId(UUID taskId, UUID staffId);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t where (t.task.id = ?1) and (t.employee.id = ?1) and (t.workingStatus.id = ?1)")
    List<TimeSheetDetailDto> getListTimeSheetDetailByTaskIdAndStaffIdAndStatusId(UUID taskId, UUID staffId, UUID statusId);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t where t.timeSheetShiftWorkPeriod.id = ?1 and t.employee.id = ?2")
    List<TimeSheetDetailDto> getListTimeSheetDetailByShiftId(UUID shiftId, UUID staffId);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t where t.employee.id = ?1 and t.timeSheetShiftWorkPeriod.id = ?2 and t.task.id = ?3")
    List<TimeSheetDetailDto> getListTimeSheetDetailByTask(UUID staffId, UUID shiftId, UUID taskId);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t where t.task.id = ?1")
    List<TimeSheetDetailDto> getListTimeSheetDetailByTaskOfItem(UUID taskId);

    @Modifying
    @Transactional
    @Query("update TimeSheetDetail t set t.task.id = null where t.task.id = ?1")
    void updateByTask(UUID taskId);

    @Modifying
    @Transactional
    @Query("delete from TimeSheetDetail tsd where tsd.employee.id = :staffId " +
            "and tsd.timeSheet.workingDate >= :startOfDay and tsd.timeSheet.workingDate <= :endOfDay")
    void deleteTimeSheetDetailOfStaffInRangeTime(@Param("staffId") UUID staffId,
                                                 @Param("startOfDay") Date startOfDay,
                                                 @Param("endOfDay") Date endOfDay);

    @Query("select distinct new com.globits.timesheet.dto.TimeSheetDetailDto(t) from TimeSheetDetail t " +
            "where t.employee.id = :staffId and t.timeSheetShiftWorkPeriod.id = :shiftWorkPeriodId")
    List<TimeSheetDetailDto> findExistedTSDInShiftWorkPeriod(@Param("staffId") UUID staffId,
                                                             @Param("shiftWorkPeriodId") UUID shiftWorkPeriodId);

    @Query("select distinct entity from TimeSheetDetail entity " +
            "where entity.employee.id = :staffId " +
            "and entity.startTime <= :createdHistoryTime and entity.endTime >= :createdHistoryTime")
    List<TimeSheetDetail> findTSDReceiveHistoryAsContent(@Param("staffId") UUID staffId,
                                                         @Param("createdHistoryTime") Date createdHistoryTime);

    @Query("select distinct entity from TimeSheetDetail entity " +
            "where entity.employee.id = :staffId " +
            "and entity.startTime >= :fromDate and entity.endTime <= :toDate")
    List<TimeSheetDetail> findTimeSheetDetailsInRangeTimeOfStaff(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
                                                                 @Param("staffId") UUID staffId);

    @Query("select distinct entity from TimeSheetDetail entity " +
            "where entity.employee.id = ?1 " +
            "and entity.startTime = ?2 ")
    List<TimeSheetDetail> findTimeSheetDetailsStaffBySync(UUID staffId, Date fromDate);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t, false) from TimeSheetDetail t where t.employee.id = ?1 and DATE(t.timeSheet.workingDate) = ?2 and t.staffWorkSchedule.id = ?3 and (t.voided is null or t.voided = false) ORDER BY t.startTime DESC")
    List<TimeSheetDetailDto> getByStaffAndDateAndSchedule(UUID staffId, Date workingDate, UUID staffWorkScheduleId);

    @Query("select entity from TimeSheetDetail entity where entity.employee.id = :staffId "
            + " and DATE(entity.timeSheet.workingDate) = DATE(:workingDate) "
            + " and entity.staffWorkSchedule.id = :staffWorkScheduleId "
            + " and (entity.voided is null or entity.voided = false) "
            + " ORDER BY entity.startTime DESC")
    List<TimeSheetDetail> getEntityListByStaffAndDateAndSchedule(@Param("staffId") UUID staffId, @Param("workingDate") Date workingDate,
                                                                 @Param("staffWorkScheduleId") UUID staffWorkScheduleId);

    /*
     * giống với getEntityListByStaffAndDateAndSchedule
     * thêm đầu vào là giao đoạn làm việc shiftWorkTimePeriodId
     */
    @Query("select entity from TimeSheetDetail entity where entity.employee.id = :staffId and DATE(entity.timeSheet.workingDate) = DATE(:workingDate) "
            + " and entity.staffWorkSchedule.id = :staffWorkScheduleId "
            + " and entity.shiftWorkTimePeriod.id = :shiftWorkTimePeriodId "
            + " and (entity.voided is null or entity.voided = false) "
            + " ORDER BY entity.startTime DESC")
    List<TimeSheetDetail> getEntityListByShiftWorkTimePeriod(@Param("staffId") UUID staffId, @Param("workingDate") Date workingDate,
                                                             @Param("staffWorkScheduleId") UUID staffWorkScheduleId, @Param("shiftWorkTimePeriodId") UUID shiftWorkTimePeriodId);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(t, false) from TimeSheetDetail t " +
            "where t.staffWorkSchedule.id = ?1 and (t.voided is null or t.voided = false) " +
            "ORDER BY t.startTime , t.endTime ")
    List<TimeSheetDetailDto> getByScheduleId(UUID staffWorkScheduleId);

    @Query("SELECT t FROM TimeSheetDetail t " +
            "WHERE t.staffWorkSchedule IS NULL and t.employee.id = :staffId " +
            "AND FUNCTION('DATE', t.startTime) = FUNCTION('DATE', :startTimeDate) " +
            "AND (t.voided IS NULL OR t.voided = FALSE)")
    List<TimeSheetDetail> findByOrphanScheduleStaffIdAndStartTimeDate(@Param("staffId") UUID staffId, @Param("startTimeDate") Date startTimeDate);


    @Query("select new com.globits.timesheet.dto.calendar.ScheduledTimesheetDetailDto(t) from TimeSheetDetail t " +
            "where t.staffWorkSchedule.id = ?1 and (t.voided is null or t.voided = false) " +
            "ORDER BY t.startTime , t.endTime ")
    List<ScheduledTimesheetDetailDto> getScheduleByScheduleId(UUID staffWorkScheduleId);

    @Query("select ts from TimeSheetDetail ts where ts.staffWorkSchedule.id =?1 and (ts.voided is null or ts.voided = false)")
    List<TimeSheetDetail> getByStaffWorkSchedule(UUID staffWorkScheduleId);

    @Query("select tsd from TimeSheetDetail tsd " +
            "where tsd.staffWorkSchedule.id = :scheduleId and tsd.employee.id = :staffId and tsd.shiftWorkTimePeriod.id = :periodId " +
            "and (tsd.voided is null or tsd.voided = false) ")
    List<TimeSheetDetail> findByStaffIdScheduleIdAndPeriodId(@Param("staffId") UUID staffId,
                                                             @Param("scheduleId") UUID scheduleId,
                                                             @Param("periodId") UUID periodId);

    @Query("select ts from TimeSheetDetail ts " +
            "where ts.staffWorkSchedule.id = :scheduleId and ts.startTime is not null and ts.endTime is null " +
            "and (ts.voided is null or ts.voided = false)")
    List<TimeSheetDetail> findUncompletedDetailsByScheduleId(@Param("scheduleId") UUID scheduleId);


}
