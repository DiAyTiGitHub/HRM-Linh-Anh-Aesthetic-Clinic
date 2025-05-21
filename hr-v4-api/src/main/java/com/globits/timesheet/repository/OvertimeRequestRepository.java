package com.globits.timesheet.repository;

import com.globits.salary.domain.SalaryResult;
import com.globits.timesheet.domain.OvertimeRequest;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequest, UUID> {
//	@Query("Select entity from OvertimeRequest entity " +
//			" JOIN entity.workSchedule workSchedule " +
//            " where workSchedule.staff.id = :staffId " +
//			" and workSchedule.shiftWork.id = :shiftWorkId " +
//            " and entity.absenceType = :absenceType" +
//            " and entity.approvalStatus = :approvalStatus")
//	List<AbsenceRequest> getListAbsenceRequest(
//            @Param("staffId") UUID staffId, @Param("shiftWorkId") UUID shiftWorkId,
//            @Param("absenceType") Integer absenceType, @Param("approvalStatus") Integer approvalStatus);



    @Query("select or FROM OvertimeRequest or " +
            "where or.staffWorkSchedule.id = :scheduleId ")
    List<OvertimeRequest> findByScheduleId(@Param("scheduleId") UUID scheduleId);
}