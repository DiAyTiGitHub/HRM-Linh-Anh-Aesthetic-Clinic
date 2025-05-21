package com.globits.timesheet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.TimeSheet;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.TimeSheetDto;
import com.globits.timesheet.dto.TimekeepingItemDto;
import com.globits.timesheet.dto.TimekeepingSummaryDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheet, UUID> {
    @Query("select new com.globits.timesheet.dto.TimeSheetDto(ts) from TimeSheet ts where ts.id=?1")
    TimeSheetDto findTimeSheetById(UUID id);

    @Query("select new com.globits.timesheet.dto.TimeSheetDetailDto(tsd) from TimeSheetDetail tsd where tsd.timeSheet.id=?1")
    Page<TimeSheetDetailDto> findTimeSheetDetailByTimeSheetId(UUID id, Pageable pageable);

    @Query("select new com.globits.timesheet.dto.TimeSheetDto(sc) from TimeSheet sc")
    Page<TimeSheetDto> getListPage(Pageable pageable);

    @Query("select ts from TimeSheet ts where date(ts.workingDate)=date(?1)")
    TimeSheet findByDate(Date date);

    @Query("select new com.globits.hr.dto.StaffDto(s) from Staff s where s.displayName like ?1")
    public Page<StaffDto> findPageByName(String staffCode, Pageable pageable);

    @Query("select new com.globits.timesheet.dto.TimeSheetDto(t) from TimeSheet t, Staff s, TimeSheetStaff ts where s.id = ts.staff.id and t.id = ts.timesheet.id and (s.displayName like %?1% or s.staffCode like %?1%)")
    public Page<TimeSheetDto> findPageByCodeOrName(String staffCode, Pageable pageable);

    @Query("select new com.globits.timesheet.dto.TimeSheetDto(s) from TimeSheet s where s.workingDate = ?1 ")
    public Page<TimeSheetDto> findPageByDate(Date workingDate, Pageable pageable);

    @Query("select new com.globits.timesheet.dto.TimeSheetDto(t) from TimeSheet t, Staff s, TimeSheetStaff ts where s.id = ts.staff.id and t.id = ts.timesheet.id and (s.displayName like %?1% or s.staffCode like %?1%) and t.workingDate = ?2 ")
    Page<TimeSheetDto> findPageByCodeAndNameAndDate(String codeAndName, Date workingDate, Pageable pageable);

    @Query("select new com.globits.timesheet.dto.TimeSheetDto(t) from TimeSheet t where t.activity.id = ?1")
    List<TimeSheetDto> getListTimeSheetByProjectActivityId(UUID id);

    @Query("select new com.globits.timesheet.dto.TimeSheetDto(t,true) from TimeSheet t where t.staff.id = ?1 order by t.workingDate desc")
    List<TimeSheetDto> getListTimeSheetByStaffId(UUID id);

    @Query("select count(t.id) from TimeSheet t where t.staff.id = ?1 order by t.workingDate desc")
    Long countTimeSheetByStaffId(UUID id);

    @Query("select new com.globits.timesheet.dto.TimeSheetDto(t) from TimeSheet t where year(t.workingDate) = ?1 and month(t.workingDate) = ?2")
    List<TimeSheetDto> getAllTimeSheetByWorkingDate(int year, int month);

    @Query("select t.staff.id from TimeSheet t where year(t.workingDate) = ?1 and month(t.workingDate) = ?2")
    List<UUID> getAllStaffIdByWorkingDate(int year, int month);

    @Query("select new com.globits.timesheet.dto.TimekeepingItemDto(t) from TimeSheet t where year(t.workingDate) = ?1 and month(t.workingDate) = ?2 and t.staff.id = ?3")
    List<TimekeepingItemDto> getTimeKeepingByMonth(int year, int month, UUID staffId);

    //find timesheet which wraps input time of staff
    @Query("select ts from TimeSheet ts where ts.staff.id = :staffId and ts.startTime <= :inputTime and ts.endTime >= :inputTime")
    List<TimeSheet> findTimeSheetWrapsInputTimeOfStaff(@Param("inputTime") Date inputTime, @Param("staffId") UUID staffId);

    @Query("select new com.globits.timesheet.dto.TimekeepingSummaryDto(t) from TimeSheet t where year(t.workingDate) = ?1 and month(t.workingDate) = ?2")
    List<TimekeepingSummaryDto> getListTimekeepingSummary(int year, int month);

    @Query("select t from TimeSheet t where DATE(t.workingDate) = DATE(:workingDate) and t.staff.id = :staffId")
    List<TimeSheet> getTimeSheetByWorkingDate(UUID staffId, Date workingDate);

    @Query("select t from TimeSheet t where t.staff.id = ?1 AND t.day = ?2 AND t.month = ?3 AND t.year= ?4")
    List<TimeSheet> getTimeSheetByWorkingDate(UUID staffId, Integer day, Integer month, Integer year);

    @Query("select distinct entity from TimeSheet entity " +
            "where (entity.staff.id = :staffId) " +
            "and (entity.workingDate >= :fromDate) " +
            "and (entity.workingDate <= :toDate) ")
    List<TimeSheet> getTimeSheetOfStaffInRangeTime(@Param("staffId") UUID staffId,
                                                   @Param("fromDate") Date fromDate,
                                                   @Param("toDate") Date toDate);

    @Query("select t from TimeSheet t " +
            "where DATE(t.workingDate) = DATE(:workingDate) " +
            "and t.staff.id = :staffId " +
            "and t.schedule.id = :staffWorkScheduleId ")
    List<TimeSheet> findTimeSheetByWorkingDate(@Param("staffId") UUID staffId, @Param("workingDate") Date workingDate, @Param("staffWorkScheduleId") UUID staffWorkScheduleId);

    @Query("select t from TimeSheet t where t.schedule.id =?1 and t.staff.id = ?2 and DATE(t.workingDate) = ?3 and (t.voided is null or t.voided = false)")
    List<TimeSheet> getByStaffWorkSchedule(UUID staffWorkScheduleId, UUID staff, Date workingDate);
}
